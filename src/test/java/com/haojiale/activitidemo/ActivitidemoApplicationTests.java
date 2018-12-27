package com.haojiale.activitidemo;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
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
     * 影响的activiti的表有哪些？
     * act_re_deployment; 部署信息
     * act_re_procdef 流程定义信息
     * act_ge_bytearray 流程定义的bpmn和png文件
     */
    @Test
    public void contextLoads() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/holiday.xml")
                .name("请假申请流程")
                .deploy();
        System.out.println("流程部署ID：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    /**
     * 启动一个流程实例
     * 影响的activiti的表有哪些？
     * act_hi_actinst 已完成的活动信息
     * act_hi_identitylink 参与者信息
     * act_hi_procinst 流程实例信息
     * act_hi_taskinst任务实例
     * act_ru_execution 执行表
     * act_ru_identitylink 参与者信息
     * act_ru_task 任务表
     */
    @Test
    public void startProcessInstance() {
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday");
        ProcessInstance processInstance = runtimeService.startProcessInstanceById("1");
        System.out.println("流程定义的ID：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例ID：" + processInstance.getId());
        System.out.println("当前活动ID：" + processInstance.getActivityId());
    }

    /**
     * 查询当前个人待执行的任务
     */
    @Test
    public void findPersonalTaskList() {
        //任务负责人
        String assignee = "zhangsan";
        //创建taskService
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee(assignee)
                .list();
        for (Task task : list) {
            System.out.println("流程实例ID：" + task.getProcessInstanceId());
            System.out.println("任务ID：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

    }

    /**
     * 任务处理
     * 影响的activiti的表有哪些？
     * act_hi_actinst
     * act_hi_identitylink
     * act_hi_taskinst*********
     * act_ru_execution
     * act_ru_identitylink
     * act_ru_task**************
     */
    @Test
    public void completeTask() {
        //任务ID
        //String taskId = "7504";
        String taskId = "5002";
        taskService.complete(taskId);
        System.out.println("完成任务ID=" + taskId);
    }

    /**
     * 查询流程定义的信息
     */
    @Test
    public void queryProcessDefinition() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("holiday")
                .orderByProcessDefinitionVersion().desc()
                .list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println("流程部署ID：" + processDefinition.getDeploymentId());
            System.out.println("流程定义ID：" + processDefinition.getId());
            System.out.println("流程定义名称：" + processDefinition.getName());
            System.out.println("流程定义key:" + processDefinition.getKey());
            System.out.println("流程定义版本：" + processDefinition.getVersion());

        }
    }

    /**
     * 删除已经部署成功的流程
     */
    @Test
    public void deleteDeployment() {
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        //repositoryService.deleteDeployment("1");
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        repositoryService.deleteDeployment("1", true);
    }

}

