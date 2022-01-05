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
//����5����user taskҪ�õ�

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
//����2�������ڹ�������ʹ��ʱ�������

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {

	//���ProcessTest���캯����Ϊ������ʹ��user task�ӵ�
	public ProcessTest() {
		super(true,true);
	}
	
	@Test
	public void testProcess() {
		RuntimeManager manager = createRuntimeManager("sample.bpmn");
		RuntimeEngine engine = getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		TaskService taskService = engine.getTaskService();//user taskҪ�õ�
		
		//����ʱ�ٶ�ϵͳ��ǰ��һ�쳡�ؿ����,���쳡���ѱ�ռ��
		//��ȡ��ǰ����
		long time = System.currentTimeMillis();
		String rent_time = new String(new SimpleDateFormat("yyyy-MM-dd").format(new Date((long)time)));
		//��ȡ��ǰ���ڵĺ�һ��
		Calendar c = Calendar.getInstance();
		Date date = new Date();
		c.setTime(date);
		c.add(Calendar.DATE, 1);
		date = c.getTime();
		String occupied_time = new String(new SimpleDateFormat("yyyy-MM-dd").format(date));

		//start a new process instance
		
		/*����1-ѧԺ��֯ѵ��school-��1�����ʱ�䱻ռ�ã�ѭ������2�ο���ͨ��-ѵ��ԭ��train*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "school");//student
		params.put("past_record", "good");//bad
		//���������̱�����ʼ��
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		
		//������krisv ȷ��rent_timeʱ�� ��һ����������죬��ռ��
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		TaskSummary task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",occupied_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//��2�� ������krisv ȷ��rent_timeʱ�䣬���죬����
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//������krisv ����������format_content train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��trainͨ��
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//ѧУ�쵼johnǩ��ͨ��
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		//�����ݹ���Աjohn����֪ͨ
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		
		/*����2-ѧԺ��֯ѵ��school-���ʱ�� �������-ѵ��ԭ�� ��1��play��ͨ����ѭ������2��trainͨ��*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "school");//student
		params.put("past_record", "good");//bad
		//���������̱�����ʼ��
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//������krisv ȷ��rent_timeʱ�� �������
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		TaskSummary task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//������krisv ����������format_content:play ��1��
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","play");//train
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶 ��1��
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��play��ͨ��
		results.put("teacher_evaluation","Don't pass.");
		taskService.complete(task.getId(), "john", results);
		
		//������krisv ����������format_content:train ��2��
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶 ��2��
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��trainͨ��
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//ѧУ�쵼johnǩ��ͨ��
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		//�����ݹ���Աjohn����֪ͨ
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		results.put("school_evaluation","very good!!!");
		taskService.complete(task.getId(), "john", results);
		
		
		/*����3-���˻ѵ��student-���ʱ�� �������-ѵ��ԭ��play ��ί��ʦ��ͨ��*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "good");//bad
		//���������̱�����ʼ��
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//������krisv ȷ��rent_timeʱ�� �������
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		TaskSummary task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//������krisv ����������format_content:play
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","play");//train
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��play��ͨ��
		results.put("teacher_evaluation","Don't pass.");
		taskService.complete(task.getId(), "john", results);
		
		
		
		/*����4-���˻ѵ��student-���ʱ�� �������-ѵ��ԭ��train-������¼bad �����ݹ���Ա��ͨ��*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "bad");//good
		//���������̱�����ʼ��
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//������krisv ȷ��rent_timeʱ�� �������
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		TaskSummary task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//������krisv ����������format_content:train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��trainͨ��
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//�����ݹ���Աmary���ѧ��������¼
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = tasks.get(0);
		System.out.println("'mary' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "mary");
		results = new HashMap<String, Object>();
		//past_record��bad��ͨ��
		results.put("administrator_evaluation","Don't pass.");
		taskService.complete(task.getId(), "mary", results);
				
				
		/*����5-���˻ѵ��student-���ʱ�� �������-ѵ��ԭ��train-������¼good �����ݹ���Աͨ��*/
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("rent_time", rent_time);
		params.put("occupied_time", occupied_time);
		params.put("format_content", "train");
		params.put("train_character", "student");//school
		params.put("past_record", "good");//bad
		//���������̱�����ʼ��
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello",params);
		
		//������krisv ȷ��rent_timeʱ�� �������
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");//��������tasks
		TaskSummary task = tasks.get(0);//��ʵ��������1��������ȡ����һ���ͳ���task����
		
		System.out.println("krisv"+" is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");//start������ִ��task
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("rent_time",rent_time);
		taskService.complete(task.getId(), "krisv", results);
		
		//������krisv ����������format_content:train
		tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
		task = tasks.get(0);
		System.out.println("krisv is doing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "krisv");
		results = new HashMap<String, Object>();
		results.put("format_content","train");//play
		taskService.complete(task.getId(), "krisv", results);
		
		//��ί��ʦjohn ��˱����д�Ƿ�淶
		tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		task = tasks.get(0);
		System.out.println("'john' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "john");
		results = new HashMap<String, Object>();
		//format_content��trainͨ��
		results.put("teacher_evaluation","very good!");
		taskService.complete(task.getId(), "john", results);
		
		//�����ݹ���Աmary���ѧ��������¼
		tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = tasks.get(0);
		System.out.println("'mary' completing task "+task.getName()+": "+task.getDescription());
		taskService.start(task.getId(), "mary");
		results = new HashMap<String, Object>();
		//past_record��goodͨ��
		results.put("administrator_evaluation","pass");
		taskService.complete(task.getId(), "mary", results);
		
		manager.disposeRuntimeEngine(engine);
		manager.close();
	}

}