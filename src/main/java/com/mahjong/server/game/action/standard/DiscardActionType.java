package com.mahjong.server.game.action.standard;

import com.mahjong.server.game.action.AbstractActionType;
import com.mahjong.server.game.context.GameContext;
import com.mahjong.server.game.enums.PlayerLocation;
import com.mahjong.server.game.object.PlayerInfo;
import com.mahjong.server.game.object.Tile;

/**
 * 动作类型“打牌”。
<<<<<<< HEAD
=======
 * 
 * @author warter
>>>>>>> refs/remotes/origin/master
 */
public class DiscardActionType extends AbstractActionType {

	@Override
	public boolean canPass(GameContext context, PlayerLocation location) {
		return false;
	}

	@Override
	protected boolean checkAliveTileSizeCondition(int size) {
		return size % 3 == 2;
	}

	@Override
	protected int getActionTilesSize() {
		return 1;
	}
	//
	// @Override
	// protected boolean isLegalActionWithPreconition(PlayerView context,
	// Tile tiles) {
	// if (!context.getMyInfo().isTing()) {
	// // 没听牌时，所有aliveTiles都可以打出
	// return true;
	// } else {
	// // 听牌后只允许打出最后摸的牌
	// Tile justDrawed = context.getJustDrawedTile();
	// return justDrawed != null
	// && justDrawed.equals(tiles.getPai()[0]);
	// }
	// }

	@Override
	protected void doLegalAction(GameContext context, PlayerLocation location, Tile tile) {
		PlayerInfo playerInfo = context.getTable().getPlayerByLocation(location);
		playerInfo.getAliveTiles().removeAll(tile);
		playerInfo.getDiscardedTiles().addTile(tile);
	}

	@Override
	protected boolean isLegalActionWithPreconition(GameContext context,PlayerLocation location, Tile tiles) {
		return true;
	}

}
