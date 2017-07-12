## Android App 在Service中保持日活跃度的策略

在Android5.0以上采用JobService，JobScheduler机制来保持app的日活跃度。
JobService和JobScheduler的机制在App的Service杀死后还有可能重新起来
（但不是百分之百，如果在android6.0以上手机开启自启动，加大重新启动服务的几率）。

在Android5.0之前版本采用AlarmManager的策略来重复启动Service来保持日活跃度。

除了这两种策略之外，还采取接收系统的广播来启动服务，接收的系统广播具体看AndroidManifest.xml。

