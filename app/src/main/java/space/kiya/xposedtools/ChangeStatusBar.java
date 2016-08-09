package space.kiya.xposedtools;
import android.graphics.Color;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
/**
 * Created by kiya on 15-12-29.
 */
public class ChangeStatusBar implements IXposedHookLoadPackage {
    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(loadPackageParam.packageName.equals("com.android.systemui")){
            findAndHookMethod("com.android.systemui.statusbar.policy.Clock", loadPackageParam.classLoader, "updateClock", new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;
                    String text = tv.getText().toString();
                    tv.setText(text + ":)");
                    tv.setTextColor(Color.RED);
                }
            });
        }
    }
}
