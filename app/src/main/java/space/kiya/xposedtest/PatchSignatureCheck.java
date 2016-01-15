package space.kiya.xposedtest;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
/**
 * Created by kiya on 16-1-15.
 */
public class PatchSignatureCheck implements IXposedHookZygoteInit{

    @Override public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod("java.security.Signature", null, "verify", byte[].class, new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("disabled verify signature.");
                param.setResult(Boolean.TRUE);
            }
        });
    }
}
