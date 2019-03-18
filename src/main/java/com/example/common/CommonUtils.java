package com.example.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.telephony.TelephonyManager.SIM_STATE_READY;

/**
 * Created by 沐沐 on 2018/10/11.
 * 公共类提取
 */

public class CommonUtils {

    public static class SystemApi{
        /**
         * 获取当前手机ip
         *
         * @return 当前ip
         */
        public static String getIp(Context mContext) throws Exception {
            String ip = "";
            try {
                // 获取wifi服务
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                // 判断wifi是否开启
                if (wifiManager.isWifiEnabled()) {
                    // wifiManager.setWifiEnabled(true);

                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    wifiInfo.getSSID();//无线网络名称
                    int ipAddress = wifiInfo.getIpAddress();
                    ip = intToIp(ipAddress);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("读取权限失败");
            }
            return ip;
        }

        private static String intToIp(int i) {
            return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
        }

        /**
         * 获取当前无线网络名称
         *
         * @return 字符串  SSID
         */
        public static String getSSID(Context mContext) throws Exception {
            try {
                // 获取wifi服务
                WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
                // 判断wifi是否开启
                if (wifiManager.isWifiEnabled()) {
                    // wifiManager.setWifiEnabled(true);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    return wifiInfo.getSSID();//无线网络名称
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("读取权限失败");
            }
            return "";
        }


        /**
         *  * 获取手机卡类型，移动、联通、电信
         *  *
         *  
         */
        private static String getMobileType(Context context) throws Exception {

            try {
                TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                String iNumeric = iPhoneManager.getSimOperator();
                if (iNumeric.length() > 0) {
                    switch (iNumeric) {
                        case "46000":
                        case "46002": // 中国移动
                            return "";
                        case "46001":
                            return "";
                        // 中国联通
                        case "46003":
                            return "";
                        // 中国电信
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("读取权限失败");
            }
            return "";
        }


        // 获取手机运营商
        public static String getProvidersName(Context mContext) {
            String ProvidersName = "nil";
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getSimState() == SIM_STATE_READY) {
                String operator = telephonyManager.getSimOperator();
                if (operator != null) {
                    switch (operator) {
                        case "46000":
                        case "46002":
                        case "46007":
                            ProvidersName = "CMCC";//中国移动
                            break;
                        case "46001":
                            ProvidersName = "CUCC";//中国联通
                            break;
                        case "46003":
                            ProvidersName = "CTCC";//中国电信
                            break;
                    }
                }
            }
            return ProvidersName;
        }

        /**
         * 获取连接网络类型
         *
         * @return 网络类型：2G,3G..
         */
        public static String getMobileNetworkType(Context mContext) {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4G";
                default:
                    return "No WiFi Or Cellular";
            }
        }



        /**
         * 网络是否连接
         *
         * @param context 上下文
         * @return boolean
         */
        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                //如果仅仅是用来判断网络连接
                //则可以使用 cm.getActiveNetworkInfo().isAvailable();
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    }
    /**
     * 像素转换
     */
    public static class PixelConversion{

        //sp转px
        public static int Sp2Px(Context context, int sp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        }

        //  dpi转px
        public static int Dp2Px(Context context, int dpi) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, context.getResources().getDisplayMetrics());
        }
    }

    /**
     * 获取apk相关的信息
     */
    public static class ApkInfoUtils {
        /**
         * 是否安装apk
         *
         * @param packagename apk包名
         * @param mContext    上下文
         * @return boolean
         */
        private boolean isApkInstalled(String packagename, Context mContext) {
            PackageManager localPackageManager = mContext.getPackageManager();
            try {
                PackageInfo localPackageInfo = localPackageManager.getPackageInfo(packagename, PackageManager.GET_UNINSTALLED_PACKAGES);
                return true;
            } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
                return false;
            }
        }

        /**
         * 获取apk信息【apkIcon,apkVersion,apkPackageName】
         *
         * @param context
         * @param apkPath
         * @return
         */
        public Drawable getApkIcon(Context context, String apkPath) {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                String label = appInfo.loadLabel(pm).toString();
                String apkVersion = info.versionName;
                String packName = appInfo.packageName;
                try {
                    return appInfo.loadIcon(pm);
                } catch (OutOfMemoryError e) {
                    Log.e("ApkIconLoader", e.toString());
                }
            }
            return null;
        }

        /**
         * 安装apk
         *
         * @param apkPath  apk路径
         * @param mContext 上下文
         */
        public void installAPK(String apkPath, Context mContext) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + apkPath),
                    "application/vnd.android.package-archive");
            mContext.startActivity(intent);
        }

        /**
         * 打开apk
         *
         * @param packagename apk包名
         */
        private void openAPK(String packagename, Context mContext) {
            PackageManager packageManager = mContext.getPackageManager();
            Intent intent = new Intent();
            intent = packageManager.getLaunchIntentForPackage(packagename);
            mContext.startActivity(intent);
        }

        private static String appHash(Context context) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), PackageManager.GET_SIGNATURES);
                byte[] cert = info.signatures[0].toByteArray();
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] publicKey = md.digest(cert);
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < publicKey.length; i++) {
                    String appendString = Integer.toHexString(0xFF & publicKey[i])
                            .toUpperCase(Locale.US);
                    if (appendString.length() == 1)
                        hexString.append("0");
                    hexString.append(appendString);
                    hexString.append(":");
                }
                String result = hexString.toString();
                return result.substring(0, result.length() - 1);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * 短信验证码样式 - 计数
     */
    public static class TimeCount extends CountDownTimer {
        private Context mContext;
        private TextView mView;
        private int textColor;
        private int backId;

        public TimeCount(Context context, TextView view) {
            super(60000, 1000);
            this.mContext = context;
            this.mView = view;
        }

        public void textColor(int txtColor) {
            textColor = txtColor;
        }

        public void backResouse(int bg) {
            backId = bg;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long l) {

            mView.setText(l / 1000 + "s后重发");
        }

        @Override
        public void onFinish() {
            mView.setBackgroundResource(backId);
            mView.setTextColor(textColor);
            mView.setClickable(true);
            mView.setText("重新获取");
        }
    }

    /**
     * 指纹功能检测
     * new FingerUtil().checkFinger();
     */
    public class FingerUtil {
        private FingerprintManager manager;
        private KeyguardManager keyguardManager;

        @TargetApi(Build.VERSION_CODES.M)
        public void checkFinger(final Context mContext, final FingerprintManager.AuthenticationCallback callback) {
            manager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);
            if (isFinger(mContext)) {
                Toast.makeText(mContext, "请验证指纹", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "无指纹识别权限", Toast.LENGTH_SHORT).show();
                } else {
                    manager.authenticate(null, null, 0, callback, null);
                }

            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        private boolean isFinger(Context context) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "无指纹识别权限", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!manager.isHardwareDetected()) { //检测硬件是否支持此功能
                Toast.makeText(context, "无指纹识别模块", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!keyguardManager.isKeyguardSecure()) {
                Toast.makeText(context, "没有开启锁屏密码,请到设置中开启", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!manager.hasEnrolledFingerprints()) { //判断是否至少录入一个指纹
                Toast.makeText(context, "暂无指纹,请到设置中录入", Toast.LENGTH_SHORT).show();

                return false;
            }
            return true;
        }


    }

    /**
     * Created by 沐沐 on 2018/3/12.
     * 生成二维码
     */

    public class QrUtils {
        /**
         * 带图片的二维码,引用zxing
         */

        public Bitmap createQRImage(String content, int heightPix, Bitmap logoBm) {
            try {
                //配置参数
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                //容错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                //设置空白边距的宽度
                hints.put(EncodeHintType.MARGIN, 1); //default is 4
                // 图像数据转换，使用了矩阵转换
                BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, heightPix, heightPix, hints);
                int[] pixels = new int[heightPix * heightPix];
                // 下面这里按照二维码的算法，逐个生成二维码的图片，
                // 两个for循环是图片横列扫描的结果
                for (int y = 0; y < heightPix; y++) {
                    for (int x = 0; x < heightPix; x++) {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * heightPix + x] = 0xff000000;
                        } else {
                            pixels[y * heightPix + x] = 0xffffffff;
                        }
                    }
                }

                // 生成二维码图片的格式，使用ARGB_8888
                Bitmap bitmap = Bitmap.createBitmap(heightPix, heightPix, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, heightPix, 0, 0, heightPix, heightPix);

                if (logoBm != null) {
                    bitmap = addLogo(bitmap, logoBm);
                }

                //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
                return bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * 在二维码中间添加Logo图案
         */
        private Bitmap addLogo(Bitmap src, Bitmap logo) {
            if (src == null) {
                return null;
            }

            if (logo == null) {
                return src;
            }

            //获取图片的宽高
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int logoWidth = logo.getWidth();
            int logoHeight = logo.getHeight();

            if (srcWidth == 0 || srcHeight == 0) {
                return null;
            }

            if (logoWidth == 0 || logoHeight == 0) {
                return src;
            }

            //logo大小为二维码整体大小的1/5
            float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
            Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
            try {
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(src, 0, 0, null);
                canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
                canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

                canvas.save(Canvas.ALL_SAVE_FLAG);
                canvas.restore();
            } catch (Exception e) {
                bitmap = null;
                e.getStackTrace();
            }

            return bitmap;
        }

        public int[] getScreenDispaly(Context context) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            int width = windowManager.getDefaultDisplay().getWidth();//
            int height = windowManager.getDefaultDisplay().getHeight();//
            int result[] = {width, height};
            return result;
        }
    }

    /**
     * Created by 沐沐 on 2016/11/29.
     * Log日志输出
     */


    public static class LogUtils {
        private static final boolean LOG_OPEN = true;
        private final static String TAG = "LOGUTILS";

        public static void i(String msg) {
            if (LOG_OPEN) {
                Log.i(TAG, msg);
            }
        }

        public static void d(String msg) {
            if (LOG_OPEN) {
                Log.d(TAG, msg);
            }
        }

        public static void e(String msg) {
            Log.e(TAG, msg);
        }

        public static void w(String msg) {
            if (LOG_OPEN) {
                Log.w(TAG, msg);
            }
        }
    }

    /**
     * Created by 沐沐 on 2016/12/1.
     * 正则表达式验证  手机号，密码，邮箱
     */

    public static class ReqularExpression {
        // 判断是否是正确手机号
        public static boolean isMobileNO(String phoneStr) {
            String regx = "^[1][3456879]\\d{9}$";
            return phoneStr.matches(regx);
        }

        //判断是否包含特殊字符
        public static boolean isNotSpecialCharacters(String pwd) {
            String re = "[a-zA-z0-9\u4E00-\u9FA5]*";
            return pwd.matches(re);
        }

        //验证邮箱
        public static boolean isValidEmail(String mail) {
            Pattern pattern = Pattern.compile("^[A-Za-z0-9][\\w\\._]*[a-zA-Z0-9]+@[A-Za-z0-9-_]+\\.([A-Za-z]{2,4})");
            Matcher mc = pattern.matcher(mail);
            return mc.matches();
        }

    }
}
