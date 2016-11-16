package space.kiya.xposedtools;
import android.content.Context;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by kiya on 16/11/15.
 */

public class LogRRDictParams implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.yyets.rrdict")){
            XposedBridge.log("found com.yyets.rrdict");

            XposedHelpers.findAndHookMethod("com.qihoo.util.StubApp3715065378", loadPackageParam.classLoader,
                    "getNewAppInstance", Context.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            XposedBridge.log("get 360 loader");
                            Context context = (Context) param.args[0];
                            final ClassLoader classLoader = context.getClassLoader();

                            XposedHelpers.findAndHookMethod("org.kymjs.kjframe.utils.CipherUtils", classLoader, "md5", String.class , new XC_MethodHook() {

                                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    XposedBridge.log("paramString:" + param.args[0]);
                                    XposedBridge.log("MD5result:" + param.getResult().toString());

                                }

                            });

                    }
            });
        }
    }
}
