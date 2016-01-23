package info.pinlab.ttada.core.model;

import info.pinlab.ttada.core.model.display.Display;
import info.pinlab.ttada.core.model.display.InstructionTextDisplay;
import info.pinlab.ttada.core.model.display.TextDisplay;
import info.pinlab.ttada.core.model.response.Response;
import info.pinlab.ttada.core.model.response.ResponseContent;
import info.pinlab.ttada.core.model.response.ResponseContentEmpty;
import info.pinlab.ttada.core.model.response.ResponseContentText;
import info.pinlab.ttada.core.model.response.ResponseHeader;
import info.pinlab.ttada.core.model.response.ResponseHeader.ResponseHeaderBuilder;
import info.pinlab.ttada.core.model.rule.StepRule;
import info.pinlab.ttada.core.model.rule.StepRule.StepRuleBuilder;
import info.pinlab.ttada.core.model.task.EssayTask;
import info.pinlab.ttada.core.model.task.InfoTask;
import info.pinlab.ttada.core.model.task.LoginTask;
import info.pinlab.ttada.core.model.task.Task;
import info.pinlab.ttada.core.model.task.TaskInstance;
import info.pinlab.ttada.core.model.task.TaskSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class TestResources {
	public static List<StepRule> srules;
	public static List<String> labels;

	public static List<TextDisplay> textdisplays;
	
	public static List<Task> tasks;
	public static List<TaskInstance> taskInstances;
//	public static List<MultichoiceTask> multichoicetasks;
	public static List<LoginTask> logintasks;
	public static List<InfoTask> infotasks;
	public static List<EssayTask> essaytasks;
	
	public static List<TaskSet> tasksets;
	public static List<Display> displays;
	
	public static int RESP_SET_SIZE = 15;
	public static List<Response> resps;
	public static List<ResponseHeader> respHeaders;
	public static List<ResponseContent> respContents;
	
	
	public static Map<Class<?>, List<?>> resourceMap;
	public static Random rand ;
	
	
	private static boolean isInitialized = false;
	
	static{
		rand = new Random(System.currentTimeMillis());
	}
	
	
	public static boolean isInitialized(){
		return isInitialized;
	}

	
	public static void clear(){
		for( List<?> list : resourceMap.values()){
			list.clear();
		}
		resourceMap.clear();
	}
	
	/**
	 * Init resources
	 */
	public static void init(){
		if(TestResources.isInitialized){
			clear();
		}

		MemCachedTest.initMemCache();
		
		resourceMap = new HashMap<Class<?>, List<?>>();

		int taskCnt = 0;
		
		displays = new ArrayList<Display>();
		resourceMap.put(Display.class, displays);

		labels = new ArrayList<String>();
		labels.add("word");
		labels.add("two words");
		labels.add("i can try to write a whole sentence here ");
		labels.add("日本語");
		labels.add("이것이 내 샌드위치 아니다");
		labels.add("<html><h1>HTML test</h1></html>");
		labels.add("");
		labels.add("!#$%&'()=~|`{}[]/*");
		resourceMap.put(String.class, labels);
		
		srules = new ArrayList<StepRule>();
		for(int i = 0 ; i < 30 ; i++){
		srules.add(new StepRuleBuilder()
				.setMaxAttempt(rand.nextInt(100)-rand.nextInt(100))
				.setNextByLastResp(rand.nextBoolean())
				.setNextByResp(rand.nextBoolean())
				.setNextByTimeout(rand.nextBoolean())
				.setNextByUsr(rand.nextBoolean())
				.setPrevByUsr(rand.nextBoolean())
				.setTimeout(rand.nextInt(100)-rand.nextInt(100))
				.build()
				);
		}
		resourceMap.put(StepRule.class, srules);

		
		textdisplays = new ArrayList<TextDisplay>();
		for(String lab : labels){
			TextDisplay text = new TextDisplay(lab);
			textdisplays.add(text);
			displays.add(text);
		}
		resourceMap.put(TextDisplay.class, textdisplays);

//		multichoicetasks = new ArrayList<MultichoiceTask>();
//		for( int i = 0 ; i < 5+rand.nextInt(5) ; i++){
//			MultichoiceTask mtask = new MultichoiceTask();
//			for(int j = 0 ; j < 1+rand.nextInt(4); j++){
//				mtask.addDisplay(textdisplays.get(rand.nextInt(textdisplays.size())));
//			}
//			for(int j = 0 ; j < 3+rand.nextInt(4); j++){
//				mtask.addChoice(labels.get(rand.nextInt(labels.size())));
//			}
//			multichoicetasks.add(mtask);
//		}
//		resourceMap.put(MultichoiceTask.class, multichoicetasks);
		
		
		tasks = new ArrayList<Task>();
		
		essaytasks = new ArrayList<EssayTask>();
		essaytasks.add(new EssayTask());
		final EssayTask essay2 = new EssayTask();
		essay2.setPrepInterval(25).setWriteInterval(60);
		essaytasks.add(essay2);
		final EssayTask essay3 = new EssayTask();
		essay3.addDisplay(new TextDisplay("An essay word!"));
		essay3.setPrepInterval(-25).setWriteInterval(-23);
		essaytasks.add(essay3);
		final EssayTask essay4 = new EssayTask();
		essay4.addDisplay(new InstructionTextDisplay("エッセー書いてください！"));
		essay4.setPrepInterval(5).setWriteInterval(15*60+2);
		essaytasks.add(essay4);
		resourceMap.put(EssayTask.class, essaytasks);

		
		
		logintasks = new ArrayList<LoginTask>();
		logintasks.add((LoginTask)new LoginTask().hasPwd(true).setBrief("login with pwd!"));
		logintasks.add((LoginTask)new LoginTask().hasPwd(false).setBrief("Just login with no paswd!"));
		resourceMap.put(LoginTask.class, logintasks);

		infotasks = new ArrayList<InfoTask>();
		for(Display disp : displays){
			InfoTask task = new InfoTask();
			task.addDisplay(disp);
			infotasks.add(task);
			task.setBrief("brief " + taskCnt++);
		}
		resourceMap.put(InfoTask.class, infotasks);
		
		
		
		tasks.addAll(logintasks);
		
		
		tasks.add(new InfoTask());
		tasks.addAll(infotasks);
		tasks.addAll(essaytasks);
		resourceMap.put(Task.class, tasks);

		taskInstances = new ArrayList<TaskInstance>();
		for (int i = 0 ; i < tasks.size() ; i++){
			taskInstances.add(
					new TaskInstance(tasks.get(i), /*taskset hash*/ 12345678, i)
					);
		}
		resourceMap.put(TaskInstance.class, taskInstances);
		
		
		tasksets = new ArrayList<TaskSet>();
		for(int t = 0 ; t < 2 + rand.nextInt(5) ; t++){ //-- generate a few TaskSets
			TaskSet tset = new TaskSet();
			for(int i = 0 ; i < 2 + rand.nextInt(10) ; i++){
				Task task = tasks.get(rand.nextInt(tasks.size()));
				tset.add(task);
				task.setBrief("brief " + taskCnt++);
			}
			tasksets.add(tset);
		}
		resourceMap.put(TaskSet.class, tasksets);
		
		
		initResponseRelatedResources();
		resourceMap.put(ResponseHeader.class, respHeaders);
		resourceMap.put(ResponseContent.class, respContents);
		resourceMap.put(Response.class, resps);

		isInitialized = true;
	}
	
	
	private static void initResponseRelatedResources(){
		respHeaders = new ArrayList<ResponseHeader>();
		respContents = new ArrayList<ResponseContent>();
		resps = new ArrayList<Response>();
				

		long time0 = System.currentTimeMillis();
		for(int i = 0; i < RESP_SET_SIZE ; i++){
			final Task task = tasks.get(rand.nextInt(tasks.size()));
			ResponseHeader header = new ResponseHeaderBuilder()
			.setTaskSetId(44)
			.setAttemptN(rand.nextInt(10))
			.setTaskIx(i)
			.setSessionId("sess")
			.setTaskId(123)
			.setUsrId("anon")
			.setTaskType(task.getClass())
			.setTimeStamp(time0++)
			.build();
			respHeaders.add(header);

			ResponseContent cont = null;
			switch (i % 2) {
			case 0:
				cont = new ResponseContentEmpty(time0++, 500+rand.nextInt(2000)/* resp T*/);
				break;
			case 1:
				cont = new ResponseContentText(time0++, 500+rand.nextInt(2000)/* resp T*/, labels.get(rand.nextInt((labels.size()))));
				break;
			default:
				break;
			}
			respContents.add(cont);
			Response resp = new Response(header, cont);
			resps.add(resp);
		}
	}
	
	static public Set<Class<?>> getAvailableClasses(){
		return  resourceMap.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public static <C> List<C> getResoursesFor(Class<C> c){
		if (resourceMap == null){
			TestResources.init();
		}
		return (List<C>)resourceMap.get(c);
	}
	
	@Test
	public void testResourceCreationButDoNothing(){
		init();
	}
	
	public static void main(String [] args){

//		BasicConfigurator.configure();
		init();
		for(Class<?> clazz : resourceMap.keySet()){
			int sz = resourceMap.get(clazz).size();
			System.out.format("Generated %16s %3d\n", clazz.getSimpleName(), sz);
		}
		
		
//		System.out.println("Testing resource creatation");
	}
}
