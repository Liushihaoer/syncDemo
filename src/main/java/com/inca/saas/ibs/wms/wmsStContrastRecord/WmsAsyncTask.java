///**
// * 
// */
//package com.inca.saas.ibs.wms.wmsStContrastRecord;
//
//import java.io.Serializable;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import com.inca.saas.CustomerDomainHolder;
//import com.inca.saas.CustomerHolder;
//import com.inca.saas.SpringContextHolder;
//import com.inca.saas.security.AsyncLoginUser;
//import com.inca.saas.security.LoginUser;
//
///**
// * @author liubin
// *
// */
//public class WmsAsyncTask implements Serializable {
//
//	public static ThreadLocal<WmsAsyncTask> localTask = new ThreadLocal<>();
//
//	public static final String STATUS_RUNNING = "running";
//	public static final String STATUS_FINISH = "finish";
//	public static final String STATUS_STOP = "stop";
//
//	String id;
//	String name;
//	String status = STATUS_RUNNING;
//	int maxStep = -1;
//	int currentStep = 0;
//	int timeout = 1;
//	String result;
//	String error;
//	boolean canStop = false;
//
//	LoginUser loginUser;
//	String cus;
//	String cusDomain;
//
//	static ValueOperations<String, WmsAsyncTask> taskCache = null;
//
//	static SimpleAsyncTaskExecutor executor;
//
//	public WmsAsyncTask() {
//		this(UUID.randomUUID().toString());
//	}
//	
//	/**
//	 * 
//	 */
//	private WmsAsyncTask(String id) {
//		super();
//
//		this.id = id;
//		loginUser = LoginUser.getLoginUser();
//		cus = CustomerHolder.get();
//		cusDomain = CustomerDomainHolder.get();
//		save();
//	}
//
//	static TaskExecutor getTaskExecutor() {
//		if (null == executor) {
//			executor = new SimpleAsyncTaskExecutor();
//			executor.setConcurrencyLimit(50);
//			executor.setThreadNamePrefix("AsyncTask");
//		}
//
//		return executor;
//	}
//
//	public WmsAsyncTask run(WmsAsyncTaskRunner run) {
//		getTaskExecutor().execute(new Runnable() {
//
//			@Override
//			public void run() {
//				localTask.set(WmsAsyncTask.this);
//				CustomerHolder.set(cus);
//				CustomerDomainHolder.set(cusDomain);
//				AsyncLoginUser.set(loginUser);
//				try {
//					result = run.run();
//				} catch (Throwable t) {
//					error = t.getMessage();
//					if (null == error) {
//						error = "未知错误";
//					}
//				} finally {
////					finish();
//					save();
//				}
//			}
//		});
//
//		return this;
//	}
//
//	public static WmsAsyncTask current() {
//		WmsAsyncTask task = localTask.get();
////		if (null == task) {
////			String id = UUID.randomUUID().toString();
////			task = new AsyncTask(id);
////			localTask.set(task);
////		}
//
//		return task;
//	}
//
//	public static WmsAsyncTask get(String id) {
//		String key = getKey(id);
//		WmsAsyncTask task = getTaskCache().get(key);
//		return task;
//	}
//
//	static String getKey(String id) {
//		String key = "saas:asynctask:" + id;
//		return key;
//	}
//
//	static ValueOperations<String, WmsAsyncTask> getTaskCache() {
//		if (null == taskCache) {
//			RedisConnectionFactory factory = SpringContextHolder.getApplicationContext()
//					.getBean(RedisConnectionFactory.class);
//			RedisTemplate<String, WmsAsyncTask> template = new RedisTemplate<>();
//			template.setConnectionFactory(factory);
//			template.setKeySerializer(new StringRedisSerializer());
//			template.setValueSerializer(new JdkSerializationRedisSerializer());
//			template.afterPropertiesSet();
//			taskCache = template.opsForValue();
//		}
//
//		return taskCache;
//	}
//
//	public WmsAsyncTask save() {
//		String key = getKey(cusDomain);
//		getTaskCache().set(key, this, getTimeout(), TimeUnit.HOURS);
//
//		return this;
//	}
//
//	public WmsAsyncTask finish() {
//		this.status = STATUS_FINISH;
//
//		return this;
//	}
//
//	/**
//	 * @return the id
//	 */
//	public String getId() {
//		return id;
//	}
//
//	/**
//	 * @return the name
//	 */
//	public String getName() {
//		return name;
//	}
//
//	/**
//	 * @param name
//	 *            the name to set
//	 */
//	public WmsAsyncTask setName(String name) {
//		this.name = name;
//		return this;
//	}
//
//	/**
//	 * @return the status
//	 */
//	public String getStatus() {
//		return status;
//	}
//
//	/**
//	 * @param status
//	 *            the status to set
//	 */
//	public WmsAsyncTask setStatus(String status) {
//		this.status = status;
//		return this;
//	}
//
//	/**
//	 * @return the maxStep
//	 */
//	public int getMaxStep() {
//		return maxStep;
//	}
//
//	/**
//	 * @param maxStep
//	 *            the maxStep to set
//	 */
//	public WmsAsyncTask setMaxStep(int maxStep) {
//		this.maxStep = maxStep;
//		return this;
//	}
//
//	/**
//	 * @return the currentStep
//	 */
//	public int getCurrentStep() {
//		return currentStep;
//	}
//
//	/**
//	 * @param currentStep
//	 *            the currentStep to set
//	 */
//	public WmsAsyncTask setCurrentStep(int currentStep) {
//		this.currentStep = currentStep;
//		return this;
//	}
//
//	/**
//	 * @return the timeout
//	 */
//	public int getTimeout() {
//		return timeout;
//	}
//
//	/**
//	 * @param timeout
//	 *            the timeout to set
//	 */
//	public WmsAsyncTask setTimeout(int timeout) {
//		this.timeout = timeout;
//		return this;
//	}
//
//	/**
//	 * @return the result
//	 */
//	public String getResult() {
//		return result;
//	}
//
//	/**
//	 * @param result
//	 *            the result to set
//	 */
//	public WmsAsyncTask setResult(String result) {
//		this.result = result;
//		return this;
//	}
//
//	/**
//	 * @return the error
//	 */
//	public String getError() {
//		return error;
//	}
//
//	/**
//	 * @param error
//	 *            the error to set
//	 */
//	public WmsAsyncTask setError(String error) {
//		this.error = error;
//		return this;
//	}
//
//}
