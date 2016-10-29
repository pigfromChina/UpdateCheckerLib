package kh.android.updatecheckerlib;

import android.app.Activity;
import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Project UpdateChecker
 * <p>
 * Created by 宇腾 on 2016/10/28.
 * Edited by 宇腾
 */

public class UpdateChecker {
    public static class UpdateInfo {
        private Market mMarket;
        private String mPackageName;
        private String mChangeLog;
        private String mVersionName;

        public String getChangeLog() {
            return mChangeLog;
        }

        public Market getMarket() {
            return mMarket;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public String getVersionName() {
            return mVersionName;
        }

        private UpdateInfo(){}
    }
    public enum Market {
        MARKET_COOLAPK,
        MARKET_GOOGLEPLAY
    }
    public interface OnCheckListener {
        void onStartCheck();
        void done (UpdateInfo info, Exception e);
    }
    public static UpdateInfo check (Market market, String packageName) throws IOException{
        Document document = null;
        UpdateInfo info = new UpdateInfo();
        info.mMarket = market;
        info.mPackageName = packageName;
        switch (market) {
            case MARKET_COOLAPK:
                document = Jsoup.connect("http://coolapk.com/apk/" + packageName).get();
                info.mVersionName = document.select("dl.dl-horizontal").get(0).select("dd").get(2).text();
                info.mChangeLog = document.select("div.ex-card-wrapper").get(3).text();
                return info;
            case MARKET_GOOGLEPLAY:
                document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName).get();
                info.mVersionName = document.select("div[itemprop=softwareVersion]").get(0).text();
                Elements elements = document.select("div.recent-change");
                String changeLog = "";
                for (Element element : elements) {
                    changeLog += element.text();
                }
                info.mChangeLog = changeLog;
                return info;
        }
        return null;
    }
    public static void checkSync (final Market market, final String packageName, final Context context, final OnCheckListener listener) {
        listener.onStartCheck();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UpdateInfo info = check(market, packageName);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.done(info, null);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.done(null, e);
                        }
                    });
                }
            }
        }).start();
    }
}
