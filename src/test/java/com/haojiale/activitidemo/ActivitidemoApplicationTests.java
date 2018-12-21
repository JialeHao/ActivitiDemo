package com.haojiale.activitidemo;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitidemoApplicationTests {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    /**
     * 部署请假流程
     * 执行此操作后 activiti会将上边代码中指定的bpm 文件保存在 activiti数据库
     */
    @Test
    public void contextLoads() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/holiday.bpmn")
                .name("请假申请流程")
                .deploy();
        System.out.println("流程部署ID："+deployment.getId());
        System.out.println("流程部署名称："+deployment.getName());
    }

    /**
     * 启动一个流程实例
     */
    @Test
    public void startProcessInstance(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday");
        System.out.println("流程定义的ID："+processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID："+processInstance.getId());
        System.out.println("当前活动ID："+processInstance.getActivityId());
    }

    /**
     * 查询当前个人待执行的任务
     */
    @Test
    public void findPersonalTaskList(){
        //任务负责人
        String assignee = "zhangsan";
        //创建taskService
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee(assignee)
                .list();
        for(Task task: list){
            System.out.println("流程实例ID："+task.getProcessInstanceId());
            System.out.println("任务ID："+task.getId());
            System.out.println("任务负责人："+task.getAssignee());
            System.out.println("任务名称："+task.getName());
        }

    }

    /**
     * 任务处理
     */
    @Test
    public void completeTask(){
        //任务ID
//        String taskId = "7504";
        String taskId = "2504";
        taskService.complete(taskId);
        System.out.println("完成任务ID="+taskId);
    }

}

