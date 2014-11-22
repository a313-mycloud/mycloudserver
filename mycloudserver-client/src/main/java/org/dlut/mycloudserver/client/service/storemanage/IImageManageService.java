/*
 * Copyright 2014 etao.com All right reserved. This software is the
 * confidential and proprietary information of etao.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with etao.com .
 */
package org.dlut.mycloudserver.client.service.storemanage;

import org.dlut.mycloudserver.client.common.MyCloudResult;
import org.dlut.mycloudserver.client.common.storemanage.ImageDTO;

/**
 * 类IImageManageService.java的实现描述：TODO 类实现描述
 * 
 * @author luojie 2014年10月27日 下午10:33:54
 */
public interface IImageManageService {

    public MyCloudResult<ImageDTO> getImageByUuid(String imageUuid);

    public MyCloudResult<Boolean> createImage(ImageDTO imageDTO);
}
