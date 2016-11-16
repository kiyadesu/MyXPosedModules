package space.kiya.xposedtools.ADBlocker;

import android.content.Context;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by kiya on 16/11/16.
 */

public class WeiboADBlock implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if (loadPackageParam.packageName.equals("com.sina.weibo")){
            XposedBridge.log("handle weibo");

            XposedHelpers.findAndHookMethod("com.sina.weibo.models.CardList", loadPackageParam.classLoader, "initFromJsonObject", JSONObject.class, hotwordsHook);
            XposedHelpers.findAndHookMethod("com.sina.weibo.composer.panel.e.a", loadPackageParam.classLoader, "b", JSONObject.class, publisherADHook);

            XposedHelpers.findAndHookMethod("com.sina.weibo.models.MBlogListBaseObject", loadPackageParam.classLoader, "getStatuses", getStatusHook);
            XposedHelpers.findAndHookMethod("com.sina.weibo.models.MBlogListBaseObject", loadPackageParam.classLoader, "setStatuses", List.class, setStatusHook);
            XposedHelpers.findAndHookMethod("java.net.URL", loadPackageParam.classLoader, "openConnection", openUrlHook);
        }

    }

    //去掉热词推荐
    XC_MethodHook hotwordsHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            JSONObject json = (JSONObject) param.args[0];

            JSONArray hotwords = json.optJSONArray("hotwords");
            JSONArray cards = json.optJSONArray("cards");

            if (null != hotwords) {
                XposedBridge.log(hotwords.toString());
                json.put("hotwords",null);
            }
            if (null != cards)  {
                XposedBridge.log(cards.toString());
                json.put("cards",null);
            }
            param.args[0] = json;
        }
    };


    //此处为发微博界面出现的图片广告
    XC_MethodHook publisherADHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            JSONObject adInfo = (JSONObject) param.args[0];
            if (adInfo != null){
                XposedBridge.log("publisher ad url:" + adInfo.getJSONObject("ad_info").getString("img_url").toString());
                param.args[0] = null;
            }
        }
    };


    // 以下为时间线中的广告,包括热门推荐和 banner
    // 部分参(chao)考(xi)现有的 weiboadblocker, 感谢大神! :p
    // 附上 weiboadblocker 链接: http://repo.xposed.info/module/com.smilehacker.weiboadblocker

    XC_MethodHook setStatusHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            ArrayList list = (ArrayList) param.args[0];
            Iterator iterator = list.iterator();

            while (iterator.hasNext()){
                String mblogtypename = (String) XposedHelpers.getObjectField(iterator.next(), "mblogtypename");
                if (mblogtypename != null && !"".equals(mblogtypename)){
                    XposedBridge.log("setStatusHook:" + mblogtypename);
                    iterator.remove();
                }
            }

            param.args[0] = list;
        }
    };

    XC_MethodHook getStatusHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            ArrayList list = (ArrayList) param.getResult();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()){
                String mblogtypename = (String) XposedHelpers.getObjectField(iterator.next(), "mblogtypename");
                if (mblogtypename != null && !"".equals(mblogtypename)){
                    XposedBridge.log("getStatusHook:" + mblogtypename);
                    iterator.remove();
                }
            }
            param.setResult(list);
        }
    };

    String[] adUrls = { "sdkapp.mobile.sina.cn", "adashx.m.taobao.com", "adashbc.m.taobao.com", "wbapp.mobile.sina.cn/wbapplua/wbpullad.lua" };

    XC_MethodHook openUrlHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            URL url = (URL) param.thisObject;
            String urlstr = url.toString();

            for (int i = 0; i < adUrls.length; i++) {
                if (urlstr.contains(adUrls[i])){
                    XposedBridge.log("url set null : " + urlstr);
                    param.setResult(null);
                    break;
                }
            }

        }
    };


}
