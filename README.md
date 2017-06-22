# CrashHandlerTest


一.意义

1.全局catch异常，保存到本地，再上传至服务器供开发人员调试程序

2.实现CrashHandler的步骤：

（1）首先创建CrashHandler类并实现UncaughtExceptionHandler接口，在它的uncaughtException方法中获取异常信息并将其存储在sd卡中或者上传到服务器
供开发人员分析。
（2）然后调用Thread的setDefaultUncaughtExceptionHandler方法将它设置为线程默认的异常处理器。

具体代码如下：


    public class CrashHandler implements Thread.UncaughtExceptionHandler {

        private static final String TAG = "CrashHandler";

        private static final boolean DEBUG = true;
        private static final String PATH = Environment.getExternalStorageDirectory() + "/CrashTest/log/";
        private static final String FILE_NAME = "crash";
        private static final String FILE_NAME_SUFFIX = ".trace";

        private static CrashHandler sInstance = new CrashHandler();
        private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
        private Context mContext;

        private CrashHandler(){
        }

        /**
         * 单例创建
         * @return
         */
        public static CrashHandler getsInstance(){
            return sInstance;
        }

        /**
         * 初始化
         * @param context
         */
        public void init(Context context){
            mContext = context;
            mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }


        /**
         * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtthread为出现未捕获异常的线程，e
         * 为未捕获的异常，有了e，我们就可以得到异常信息
         * @param t
         * @param e
         */
        @Override
        public void uncaughtException(Thread t, Throwable e) {

            try {
                //导出异常信息到SD卡中
                dumpExceptionToSDCard(e);
                //上传异常信息到服务器
                uploadExceptionToServer();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            e.printStackTrace();
            //如果系统提供了默认的异常处理器,则交给系统去结束程序,否则就由自己结束自己
            if(mDefaultCrashHandler != null){
                mDefaultCrashHandler.uncaughtException(t,e);
            }else {
                Process.killProcess(Process.myPid());//结束进程
            }


        }


        /**
         * 导出异常到SD卡
         * @param e
         */
        private void dumpExceptionToSDCard(Throwable e) throws IOException{
            //如果sd卡不存在或者无法使用，则无法把异常信息写入sd卡
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                if(DEBUG){
                    Log.w(TAG, "sdcard unmounted,skip dump exception" );
                    return;
                }
            }

            //创建文件目录
            File dir = new File(PATH);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //创建文件
            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
            File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);

            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pw.println(time);
                dumpPhoneInfo(pw);
                pw.println();
                e.printStackTrace(pw);
                pw.close();
            } catch (Exception e1) {
                Log.e(TAG,"dump crash into failed");
            }


        }

        /**
         *  输出手机信息
         * @param pw
         */
        private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {

            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);

            pw.print("App Version:");
            pw.print(pi.versionName);
            pw.print('_');
            pw.println(pi.versionCode);

            //androdi版本号
            pw.print("OS Version:");
            pw.print(Build.VERSION.RELEASE);
            pw.print('_');
            pw.println(Build.VERSION.SDK_INT);

            //手机制造商
            pw.print("Vendor: ");
            pw.println(Build.MANUFACTURER);

            //手机型号
            pw.print("Model: ");
            pw.println(Build.MODEL);

            //cpu架构
            pw.print("CPU ABI: ");
            pw.println(Build.CPU_ABI);

        }


        private void uploadExceptionToServer() {
            //TODO Upload Exception Message To Your Web Server
        }


    }



（3）在Application初始化的时候为线程设置CrashHandler
 
         public void onCreate() {
                super.onCreate();

                myApplicaiton = this;

                //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
                CrashHandler crashHandler = CrashHandler.getsInstance();
                crashHandler.init(this);


            }











