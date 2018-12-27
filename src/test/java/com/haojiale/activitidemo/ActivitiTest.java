package com.haojiale.activitidemo;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description 在代码中实现学生进行请假申请，班主任审核，教务处审核
 * @Author haojiale
 * @Date 2018/12/24 18:44
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署请假流程 影响的activiti的表有哪些？
     * act_ge_bytearray 流程定义的bpmn和png文件
     * act_re_deployment 部署信息
     * act_re_procdef 流程定义信息
     */
    @Test
    public void buildProcess(){
        Deployment deployment = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("processes/holiday.bpmn")
                .addClasspathResource("processes/holiday.png")
                .deploy();
        System.out.println("流程部署的ID："+deployment.getId());
        System.out.println("流程部署的名称："+deployment.getName());
    }

    /**
     * 启动一个流程实例 影响的activiti的表有哪些？
     * act_hi_actinst 已完成的活动信息
     * act_hi_identitylink 参与者信息
     * act_hi_procinst 流程实例信息
     * act_hi_taskinst任务实例
     * act_ru_execution 执行表
     * act_ru_identitylink 参与者信息
     * act_ru_task 任务表
     */
    @Test
    public void startProcessInstance(){
        runtimeService.startProcessInstanceById("holiday:1:4"); //这个是查看数据库中act_re_procdef表
    }

    /**
     * 完成任务
     */
    @Test
    public void completeTask(){
        taskService.complete("7502"); //查看act_ru_task任务表 act_hi_taskinst任务实例表的ID
    }

    /**
     * 查询某个人待执行的任务
     */
    @Test
    public void queryTask(){
        List<Task> list = taskService.createTaskQuery()
                .taskAssignee("大毛")
                .list();
        for(Task task : list){
            System.out.println("任务ID："+task.getId());
            System.out.println("任务名称："+task.getName());
        }
    }
}
