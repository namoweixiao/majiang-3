package com.mahjong.server.netty.handler;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mahjong.server.entity.UserInfo;
import com.mahjong.server.game.context.HouseContext;
import com.mahjong.server.game.context.RoomContext;
import com.mahjong.server.game.enums.EventEnum;
import com.mahjong.server.game.enums.PlayerLocation;
import com.mahjong.server.game.object.PlayerInfo;
import com.mahjong.server.netty.model.KillRoomNoticeRespModel;
import com.mahjong.server.netty.model.KillRoomReqModel;
import com.mahjong.server.netty.model.KillRoomRespModel;
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
public class KillRoomHandler extends SimpleChannelInboundHandler<ProtocolModel> {
	@Autowired
	private DBService dbService;
	@Override
	public void channelRead0(ChannelHandlerContext ctx, ProtocolModel protocolModel) throws Exception {
		if (protocolModel.getCommandId() == EventEnum.KILL_ROOM_REQ.getValue()) {
			if (protocolModel.getBody() == null) {
				ctx.close();
			} else {
				KillRoomReqModel killRoomReqModel = JSON.parseObject(new String(protocolModel.getBody(), "UTF-8"),
						new TypeReference<KillRoomReqModel>() {
						});
				
				String weixinId = killRoomReqModel.getWeiXinId();
				Boolean isAggree = killRoomReqModel.isAggree();
				UserInfo userInfo = dbService.selectUserInfoByWeiXinMark(weixinId);
				
				RoomContext roomContex = HouseContext.weixinIdToRoom.get(weixinId);
				
				if(roomContex.getAgreeKillRoomNum().intValue()==0){
					// 通知其他三家
					for(Entry<PlayerLocation, PlayerInfo>  ent : roomContex.getGameContext().getTable().getPlayerInfos().entrySet()){
						
						PlayerInfo playerIn = ent.getValue();
						
						if(playerIn.getUserInfo().getWeixinMark().equals(weixinId)){
							continue;
						}
						
						ProtocolModel newProtocolModel = new ProtocolModel();
						newProtocolModel.setCommandId(EventEnum.KILL_ROOM_NOTICE_RESP.getValue());
						
						KillRoomNoticeRespModel killRoomNoticeRespModel = new KillRoomNoticeRespModel();
						killRoomNoticeRespModel.setNickName(userInfo.getNickName());
						
						newProtocolModel.setBody(JSON.toJSONString(killRoomNoticeRespModel).getBytes("UTF-8"));
						
						ChannelHandlerContext userCtx = ClientSession.sessionMap.get(weixinId);
						userCtx.writeAndFlush(newProtocolModel);
						
					}
				}
				if(isAggree){
					roomContex.getAgreeKillRoomNum().incrementAndGet();
				}else{
					roomContex.getDisagreeUserNames().add(userInfo.getNickName());
					roomContex.getDisAgreeKillRoomNum().incrementAndGet();
				}
				
				if((roomContex.getAgreeKillRoomNum().intValue()+roomContex.getDisAgreeKillRoomNum().intValue()) == 4){
					
					KillRoomRespModel killRoomRespModel = new KillRoomRespModel();
					
					if(roomContex.getDisAgreeKillRoomNum().intValue()==0){
						
						killRoomRespModel.setResult(true);
						killRoomRespModel.setMsg("解散成功！");
						
						HouseContext.rommList.remove(roomContex.getRoomNum());
						
						for(Entry<PlayerLocation, PlayerInfo>  ent : roomContex.getGameContext().getTable().getPlayerInfos().entrySet()){
							PlayerInfo playerIn = ent.getValue();
							HouseContext.weixinIdToRoom.remove(playerIn.getUserInfo().getWeixinMark());
						}
						
						
					}else{
						killRoomRespModel.setResult(false);
						killRoomRespModel.setMsg("解散失败！");
						killRoomRespModel.setUnAgreeNickNames(roomContex.getDisagreeUserNames());
						
						/**
						 * 清空本次记录
						 */
						roomContex.setAgreeKillRoomNum(new AtomicInteger(0));
						roomContex.setDisAgreeKillRoomNum(new AtomicInteger(0));
						roomContex.setDisagreeUserNames(new ArrayList<String>());
					}
					
					protocolModel.setCommandId(EventEnum.KILL_ROOM_RESP.getValue());
					protocolModel.setBody(JSON.toJSONString(killRoomRespModel).getBytes("UTF-8"));
					ctx.writeAndFlush(protocolModel);
					
				}
				
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