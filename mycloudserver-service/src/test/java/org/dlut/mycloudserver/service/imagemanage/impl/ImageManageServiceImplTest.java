/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.service.imagemanage.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;
import org.dlut.mycloudserver.client.common.storemanage.StoreFormat;
import org.dlut.mycloudserver.client.service.storemanage.IImageManageService;
import org.dlut.mycloudserver.service.BaseTestCase;
import org.junit.Test;
import org.libvirt.LibvirtException;
import org.mycloudserver.common.util.TemplateUtil;

/**
 * 类ImageManageServiceImplTest.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月22日 下午4:30:27
 */
public class ImageManageServiceImplTest extends BaseTestCase {

    @Resource(name = "imageManageService")
    private IImageManageService imageManageService;

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.ImageManageServiceImpl#getImageByUuid(java.lang.String)}
     * .
     */
    @Test
    public void testGetImageByUuid() {
        MyCloudResult<ImageDTO> result = imageManageService.getImageByUuid("288f4ee6-42f6-4d8a-8e47-46bce4431ab1");
        printObject(result);
    }

    /**
     * Test method for
     * {@link org.dlut.mycloudserver.service.storemanage.impl.ImageManageServiceImpl#createImage(org.dlut.mycloudserver.client.common.storemanage.ImageDTO)}
     * .
     */
    @Test
    public void testCreateImage() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageFormat(StoreFormat.QCOW2);
        imageDTO.setImageName("ubuntu12.04");
        imageDTO.setImagePath("/home/luojie/mycloud-store/515359f5-979a-491d-962c-10bf112eb176");
        //        imageDTO.setImageSize(1258686L);
        imageDTO.setImageUuid(UUID.randomUUID().toString());
        imageDTO.setIsTemplagte(true);
        MyCloudResult<Boolean> result = imageManageService.createImage(imageDTO);
        printObject(result);
    }

    @Test
    public void testUpdateImage() {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setImageName("testupdate");
        imageDTO.setImageUuid("843148d6-fd5c-4a92-8550-53de9dd60218");
        MyCloudResult<Boolean> result = imageManageService.updateImage(imageDTO);
        printObject(result);
    }

    @Test
    public void testVelocity() throws IOException {
        VelocityEngine ve = new VelocityEngine();
        String path = "src/test/resources/volume.xml";
        //        ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path);
        ve.setProperty(Velocity.INPUT_ENCODING, "utf8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "utf8");
        ve.init();
        Template template = ve.getTemplate(path);
        VelocityContext context = new VelocityContext();
        context.put("name", "luojie");
        StringWriter strWriter = new StringWriter();
        template.merge(context, strWriter);
        printObject(strWriter.toString());
    }

    @Test
    public void testVelocityUtil() {
        String path = "src/test/resources/volume.xml";
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("name", "huihui");
        String result = TemplateUtil.renderTemplate(path, context);
        printObject(result);
    }

    @Test
    public void testClone() throws LibvirtException, MagicParseException, MagicMatchNotFoundException, MagicException {

        //        printObject(CommonUtil.createUuid());
        File image = new File("/home/luojie/mycloud-store/515359f5-979a-491d-962c-10bf112eb176");
        printObject(image.length());
        MagicMatch match = Magic.getMagicMatch(image, true);
        printObject(match.getMimeType());
        //        Connect conn = new Connect("qemu:///system");
        //        StoragePool pool = conn.storagePoolLookupByName("default");
    }

}
