package com.sample;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
//以上5行是user task要用的

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
//以上2行用于在工作流中使用时间和日期

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {

	//这个ProcessTest构造函数是为了正常使用user task加的
	public ProcessTest() {
		super(true,true);
	}
	
	@Test
	public void testProcess() {
		RuntimeManager manager = createRuntimeManager("sample.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();//user task要用的
		
		//测试时假定系统当前这一天场地可租借,明天场地已被占用
		//获取当前日期
		long time = System.currentTimeMillis();
		String rent_time = new String(new SimpleDateFormat("yyyy-MM-dd").format(new Date((long)time)));
		//获取当前日期的后一天
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		String occupied_time = new String(new SimpleDateFormat("yyyy-MM-dd").format(date));

		//start a new process instance
		
		/*测试1-学院组织训练school-第1次租借时间被占用，循环，第2次可以通过-训练原因train*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "school");//student
		params.put("past_record", "good");//bad
		//上面是流程变量初始化
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		
		//负责人krisv 确定rent_time时间 第一次填的是明天，被占用
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		TaskSummary task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",occupied_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//第2次 负责人krisv 确定rent_time时间，今天，可用
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//负责人krisv 填租借表内容format_content train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是train通过
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//学校领导john签字通过
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		//体育馆管理员john接收通知
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		
		/*测试2-学院组织训练school-租借时间 今天可用-训练原因 第1次play不通过，循环，第2次train通过*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "school");//student
		params.put("past_record", "good");//bad
		//上面是流程变量初始化
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//负责人krisv 确定rent_time时间 今天可用
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		TaskSummary task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//负责人krisv 填租借表内容format_content:play 第1次
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","play");//train
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范 第1次
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是play不通过
		results.put("teacher_evaluation","Don't pass.");
		taskService.complete(task.getId(), "john", results);
		
		//负责人krisv 填租借表内容format_content:train 第2次
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范 第2次
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是train通过
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//学校领导john签字通过
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		//体育馆管理员john接收通知
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		
		/*测试3-个人活动训练student-租借时间 今天可用-训练原因play 团委老师不通过*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "good");//bad
		//上面是流程变量初始化
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//负责人krisv 确定rent_time时间 今天可用
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		TaskSummary task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//负责人krisv 填租借表内容format_content:play
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","play");//train
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是play不通过
		results.put("teacher_evaluation","Don't pass.");
		taskService.complete(task.getId(), "john", results);
		
		
		
		/*测试4-个人活动训练student-租借时间 今天可用-训练原因train-过往记录bad 体育馆管理员不通过*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "bad");//good
		//上面是流程变量初始化
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//负责人krisv 确定rent_time时间 今天可用
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		TaskSummary task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//负责人krisv 填租借表内容format_content:train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是train通过
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//体育馆管理员mary检查学生过往记录
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = tasks.get(0);
		System.out.println("'mary' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "mary");
		results = new HashMap<String, Object>();
		//past_record是bad不通过
		results.put("administrator_evaluation","Don't pass.");
		taskService.complete(task.getId(), "mary", results);
				
				
		/*测试5-个人活动训练student-租借时间 今天可用-训练原因train-过往记录good 体育馆管理员通过*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "good");//bad
		//上面是流程变量初始化
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//负责人krisv 确定rent_time时间 今天可用
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//获得链表的tasks
		TaskSummary task = tasks.get(0);//其实链表长度是1，那我们取出第一个就成了task对象
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start函数来执行task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//负责人krisv 填租借表内容format_content:train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//团委老师john 审核表格填写是否规范
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content是train通过
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//体育馆管理员mary检查学生过往记录
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = tasks.get(0);
		System.out.println("'mary' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "mary");
		results = new HashMap<String, Object>();
		//past_record是good通过
		results.put("administrator_evaluation","pass");
		taskService.complete(task.getId(), "mary", results);
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}