package com.haojiale.activitidemo;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @Description 部署流程的几种方式
 * @Author haojiale
 * @Date 2018/12/27 11:30
 * @Version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProcessDeployTest {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 通过bpmn和png资源进行部署
     */
    @Test
    public void deployFromClasspath() {
        //得到流程引擎
        repositoryService.createDeployment()
                .name("xxx流程")
                .addClasspathResource("processes/holiday.bpmn")
                .addClasspathResource("processes/holiday.png")
                .deploy();
    }

    /**
     * 通过 inputstream完成部署
     */
    @Test
    public void deployFromInputStream(){
        InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("processes/holiday.bpmn");
        repositoryService.createDeployment()
                .name("AAA流程")
                .addInputStream("processes/holiday.bpmn", bpmnStream)
                .deploy();
    }

    /**
     * 通过zipinputstream完成部署
     * 注意：这个的话，需要将bpmn和png文件进行压缩成zip文件，然后放在项目src目录下即可(当然其他目录也可以)
     */
    @Test
    public void deployFromZipinputStream(){
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("holiday.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 查询所有的部署流程
     * act_re_deployment 部署信息
     */
    @Test
    public void queryAllDeplyoment(){
        List<Deployment> list = repositoryService.createDeploymentQuery()
                .orderByDeploymenTime()//按照部署时间排序
                .desc()//按照降序排序
                .list();
        for (Deployment deployment:list) {
            System.out.println("流程部署的ID："+deployment.getId());
            System.out.println("流程部署的名称："+deployment.getName());
            System.out.println("********************************");
        }
    }

    /**
     * 删除已经部署的Activiti流程
     */
    @Test
    public void deleteDeploy(){
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        //repositoryService.deleteDeployment("1");
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        repositoryService.deleteDeployment("12501",true);
    }

    /**
     * 根据名称查询流程部署
     */
    @Test
    public void queryDeploymentByName(){
        List<Deployment> deployments = repositoryService.createDeploymentQuery()
                .orderByDeploymenTime()//按照部署时间排序
                .desc()//按照降序排序
                .deploymentName("请假流程")
                .list();
        for (Deployment deployment : deployments) {
            System.out.println(deployment.getId());
        }
    }

    /**
     * 查询所有的流程定义
     */
    @Test
    public void queryAllProcessDefine(){
        List<ProcessDefinition> pdList = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        for (ProcessDefinition pd : pdList) {
            System.out.println(pd.getName());
        }
    }

    /**
     * 查看流程图
     * 根据deploymentId和name(在act_ge_bytearray数据表中)
     */
    @Test
    public void showImage() throws Exception{
        InputStream inputStream = repositoryService.getResourceAsStream("801","processes/holiday.png");
        OutputStream outputStream3 = new FileOutputStream("e:/processimg.png");
        int b = -1 ;
        while ((b=inputStream.read())!=-1){
            outputStream3.write(b);
        }
        inputStream.close();
        outputStream3.close();
    }
    /**
     * 根据pdid查看图片(在act_re_procdef数据表中)
     * @throws Exception
     */
    @Test
    public void showImage2() throws Exception{
        InputStream inputStream = repositoryService.getProcessDiagram("shenqing:1:804");
        OutputStream outputStream = new FileOutputStream("e:/processimg.png");
        int b = -1 ;
        while ((b=inputStream.read())!=-1){
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
    }
    /**
     * 查看bpmn文件(在act_re_procdef数据表中)
     */
    @Test
    public void showBpmn() throws Exception{
        InputStream inputStream = repositoryService.getProcessModel("shenqing:1:804");
        OutputStream outputStream = new FileOutputStream("e:/processimg.bpmn");
        int b = -1 ;
        while ((b=inputStream.read())!=-1){
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
    }
}
