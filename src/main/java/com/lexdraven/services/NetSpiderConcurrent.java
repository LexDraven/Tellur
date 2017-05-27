package com.lexdraven.services;

import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NetSpiderConcurrent {
    private boolean logToConsole = false;
    private boolean needToCheckImages = false;
    private ConcurrentLinkedDeque<String> linksInDomain;
    private ConcurrentLinkedDeque<String> allLinks;
    private ConcurrentLinkedDeque<String> mainLink;
    private ConcurrentHashMap<String, String> brokenLinks;
    private ConcurrentHashSet<String> checkedLinks;
    private String mainDomain;
    private String link;
    private String[] ignoreList = new String[]{"http://reddit.com/submit?url", "https://www.linkedin.com", "https://msdn.microsoft.com/en-us/", "https://help.salesforce.com/apex/", "https://www.linkedin.com/company/capsidea",
            "mailto", "https://iwantmyname.com/", "http://www.postgresql.org/docs/", "http://support.pipedrive.com/hc/en-us/articles/207344545-How-to-find-your-personal-API-key"
            , "http://selvakumar.me/script-to-auto-login-remote-servers/", "https://www.quora.com/topic/", "http://www.chiark.greenend.org.uk", "https://console.aws.amazon.com/",
            "https://support.mozilla.org/en-US/kb/enable-and-disable-cookies", "http://www.facebook.com/sharer.php?", "http://vkontakte.ru/share.php?", "http://www.tumblr.com/share/",
            "https://support.microsoft.com/en-us/help/"};

    public NetSpiderConcurrent(String mainDomain) {
        this.mainDomain = mainDomain;
        System.setProperty("jsse.enableSNIExtension", "false");
        linksInDomain = new ConcurrentLinkedDeque<>();
        brokenLinks = new ConcurrentHashMap<>();
        checkedLinks = new ConcurrentHashSet<>();
        allLinks = new ConcurrentLinkedDeque<>();
        mainLink = new ConcurrentLinkedDeque<>();
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }

    public void setNeedToCheckImages(boolean needToCheckImages) {
        this.needToCheckImages = needToCheckImages;
    }

    private boolean isInIgnoreList(String url) {
        for (String listItem : ignoreList) {
            if (url.startsWith(listItem)) {
                return true;
            }
        }
        return false;
    }

    private void startThreads(int number) {
        Thread[] pool = new Thread[number];
        for (int i = 0; i < number; i++) {
            pool[i] = new Thread(new CheckRunner(this));
            pool[i].setDaemon(true);
        }
        for (Thread tred : pool) {
            tred.start();
        }
    }

    public boolean checkLinksInDomain() {
        linksInDomain.add(mainDomain);
        checkedLinks.add(mainDomain);
        long begin = System.currentTimeMillis();
        startThreads(2);
        while ((!linksInDomain.isEmpty()) | (!allLinks.isEmpty())) {
            if (!linksInDomain.isEmpty()) {
                link = linksInDomain.poll();
                if (link.endsWith("/")) {
                    link = link.substring(0, link.length() - 1);
                }
                if (logToConsole) System.out.println("Go to page " + link);
                getAllLinks();
            }
        }
        CheckRunner.setNumber(0);
        while (CheckRunner.getCount() > 0) ;
        System.out.println("Checked links: " + checkedLinks.size());
        System.out.println("Broken links: " + brokenLinks.size());
        pause();
        if (brokenLinks.size() > 0) {
            System.out.println("Broken links are:");
            pause();
            for (Map.Entry<String, String> name : brokenLinks.entrySet()) {
                System.err.println(name.getKey() + " - " + name.getValue());
            }
        }
        long time = (System.currentTimeMillis() - begin) / 1000;
        pause();
        System.out.println("Total time spent: " + time + " sec");
        return brokenLinks.size() == 0;
    }

    private void pause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getAllLinks() {
        HashSet<String> result;
        FastRequest request = new FastRequest();
        if (needToCheckImages) {
            result = request.getLinksFromUrl(link, mainDomain, "img", "src");
            if (result.size() > 0) {
                result = removeIgnore(result);
                putToLinksDeques(result);
            }
        }
        result = request.getLinksFromUrl(link, mainDomain, "a", "href");
        if (result.size() > 0) {
            result = removeIgnore(result);
            putToLinksDeques(result);
        }
    }

    private HashSet<String> removeIgnore(HashSet<String> strings) {
        Iterator<String> iterator = strings.iterator();
        while (iterator.hasNext()) {
            String link = iterator.next();
            if (isInIgnoreList(link)) {
                iterator.remove();
            }
        }
        return strings;
    }

    private void putToLinksDeques(Set<String> list) {
        for (String element : list) {
            allLinks.add(element);
            mainLink.add(link);
        }
    }

    ConcurrentLinkedDeque<String> getLinksInDomain() {
        return linksInDomain;
    }

    ConcurrentLinkedDeque<String> getMainLink() {
        return mainLink;
    }

    ConcurrentLinkedDeque<String> getAllLinksQueue() {
        return allLinks;
    }

    ConcurrentHashMap<String, String> getBrokenLinks() {
        return brokenLinks;
    }

    public String getMainDomain() {
        return mainDomain;
    }

    ConcurrentHashSet<String> getCheckedLinks() {
        return checkedLinks;
    }
}

class CheckRunner implements Runnable {
    private NetSpiderConcurrent spider;
    private static volatile int number = 1;
    private static volatile int count;
    private URLChecker checker;

    CheckRunner(NetSpiderConcurrent spiderConcurrent) {
        this.spider = spiderConcurrent;
        number++;
        checker = new URLChecker();
    }

    static void setNumber(int number) {
        CheckRunner.number = number;
    }

    public static int getCount() {
        return count;
    }

    @Override
    public void run() {
        count++;
        while (number > 0) {
            sortByGroups();
        }
        count--;
    }

    private void sortByGroups() {
        if (!spider.getAllLinksQueue().isEmpty()) {
            String url = spider.getAllLinksQueue().poll();
            String mainPage = spider.getMainLink().poll();
            if ((url == null) || (mainPage == null)) {
                return;
            }
            if (!spider.getCheckedLinks().contains(url)) {
                spider.getCheckedLinks().add(url);
                try {
                    if (!(checkLinks(url))) {
                        spider.getBrokenLinks().put(url, mainPage);
                    } else {
                        if ((url.startsWith(spider.getMainDomain()) & (!checker.isFile(url)))) {
                            spider.getLinksInDomain().add(url);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error adding links! " + e.getMessage());
                }
            }
        }
    }

    private boolean checkLinks(String URL) {
        return checker.checkLinks(URL);
    }
}
