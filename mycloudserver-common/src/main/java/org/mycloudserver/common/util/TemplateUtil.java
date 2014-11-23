/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.mycloudserver.common.util;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

/**
 * 类TemplateUtil.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年11月23日 下午1:12:14
 */
public class TemplateUtil {

    public static String renderTemplate(String templatePath, Map<String, Object> context) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(Velocity.INPUT_ENCODING, "utf8");
        velocityEngine.setProperty(Velocity.OUTPUT_ENCODING, "utf8");
        velocityEngine.init();
        Template template = velocityEngine.getTemplate(templatePath);
        VelocityContext velContext = new VelocityContext(context);
        StringWriter writer = new StringWriter();
        template.merge(velContext, writer);
        return writer.toString();
    }
}
