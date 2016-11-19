package kh.android.updatecheckerlib;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;

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
        MARKET_GOOGLEPLAY,
        MARKET_WANDOUJIA
    }
    public interface OnCheckListener {
        void onStartCheck();
        void done (UpdateInfo info, Exception e);
    }
    @RequiresPermission(Manifest.permission.INTERNET)
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
            case MARKET_WANDOUJIA :
                document = Jsoup.connect("http://www.wandoujia.com/apps/" + packageName).get();
                info.mVersionName = document.select("div[class=con]").get(0).text().substring(3);
                info.mChangeLog = document.select("div[class=con]").get(1).text();
                return info;
        }
        return null;
    }

    private UpdateInfo info;
    private OnCheckListener listener;
    private CheckTask mTask;

    public UpdateChecker (Market market, String packageName) {
        info = new UpdateInfo();
        info.mMarket = market;
        info.mPackageName = packageName;
    }
    public UpdateChecker () {}
    public UpdateChecker (UpdateInfo info) {
        this.info = info;
    }
    @RequiresPermission(Manifest.permission.INTERNET)
    public void checkAsync (final Market market, final String packageName, final OnCheckListener listener) {
        this.listener = listener;
        if (info == null) {
            info = new UpdateInfo();
            info.mMarket = market;
            info.mPackageName = packageName;
        }
        mTask = new CheckTask();
        mTask.execute();
    }
    public void checkAsync (OnCheckListener listener) {
        this.listener = listener;
        if (info == null)
            throw new NullPointerException("Update Info mustn't null!");
        mTask = new CheckTask();
        mTask.execute();
    }
    public void stop () {
        if (mTask != null)
            mTask.cancel(true);
    }

    private class CheckTask extends AsyncTask<Void, Void, Object> {
        @Override
        protected Object doInBackground(Void... voids) {
            try {
                return check(info.getMarket(), info.getPackageName());
            } catch (Exception e) {
                return e;
            }
        }

        @Override
        protected void onPreExecute () {
            listener.onStartCheck();
        }

        @Override
        protected void onPostExecute (Object obj) {
            if (obj instanceof UpdateInfo) {
                listener.done((UpdateInfo)obj, null);
            } else {
                listener.done(null, (Exception)obj);
            }
        }
    }
}
