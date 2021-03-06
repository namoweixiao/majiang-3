package com.mahjong.server.netty.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mahjong.server.entity.UserInfo;
import com.mahjong.server.game.context.HouseContext;
import com.mahjong.server.game.context.RoomContext;
import com.mahjong.server.game.enums.EventEnum;
import com.mahjong.server.netty.model.CreateRoomReqModel;
import com.mahjong.server.netty.model.CreateRoomRespModel;
import com.mahjong.server.netty.model.ProtocolModel;
import com.mahjong.server.netty.session.ClientSession;
import com.mahjong.server.service.DBService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 登陆认证
 *
 */
@Component
public class CreateRoomHandler extends SimpleChannelInboundHandler<ProtocolModel> {
	@Autowired
	private DBService dbService;
	@Override
	public void channelRead0(ChannelHandlerContext ctx, ProtocolModel protocolModel) throws Exception {
		if (protocolModel.getCommandId() == EventEnum.CREATE_ROOM_REQ.getValue()) {
			if (protocolModel.getBody() == null) {
				ctx.close();
			} else {
				CreateRoomReqModel createRoomReqModel = JSON.parseObject(new String(protocolModel.getBody(), "UTF-8"),
						new TypeReference<CreateRoomReqModel>() {
						});
				
				String weixinId = createRoomReqModel.getWeiXinId();
				UserInfo userInfo = dbService.selectUserInfoByWeiXinMark(weixinId);
				ctx = ClientSession.sessionMap.get(weixinId);
				
				String ruleStrategy = createRoomReqModel.getRuleStrategy();
				int fangKaStrategy = createRoomReqModel.getFangKaStrategy();
				RoomContext roomContex = null;
				if((roomContex = HouseContext.weixinIdToRoom.get(weixinId))==null){
					roomContex =  HouseContext.addRoom( userInfo, ruleStrategy, fangKaStrategy);
					HouseContext.weixinIdToRoom.put(weixinId, roomContex);
				}
				
				CreateRoomRespModel createRoomRespModel = new CreateRoomRespModel(weixinId, true,roomContex);
				protocolModel.setCommandId(EventEnum.CREATE_ROOM_RESP.getValue());
				protocolModel.setBody(JSON.toJSONString(createRoomRespModel).getBytes("UTF-8"));
				ctx.writeAndFlush(protocolModel);
				
			}
		} else {
			ctx.fireChannelRead(protocolModel);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}
	
}