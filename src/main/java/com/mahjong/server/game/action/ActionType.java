package com.mahjong.server.game.action;

import java.util.Collection;

import com.mahjong.server.exception.IllegalActionException;
import com.mahjong.server.game.action.standard.StandardActionType;
import com.mahjong.server.game.context.GameContext;
import com.mahjong.server.game.enums.PlayerLocation;
import com.mahjong.server.game.object.Tile;

/**
 * 做出动作的类型。
 * 
 * @author warter
 */
public interface ActionType {

	public String name();


	/**
	 * 返回真正的动作类型对象。<br>
	 * 默认实现为返回此对象。如果不是这样（例如{@link StandardActionType}）则需要重写此方法。
	 */
	public ActionType getRealTypeObject();

	/**
	 * 判断指定状态下指定位置的玩家可否做此种类型的动作。
	 * 
	 * @return 能做返回true；否则返回false。
	 */
	public boolean canDo(GameContext context, PlayerLocation location);

	/**
	 * 返回此动作是否可以放弃。
	 * 
	 * @return 可以放弃返回true；否则返回false。
	 */
	public boolean canPass(GameContext context, PlayerLocation location);

	/**
	 * 返回一个集合，包含指定状态下指定玩家可作出的此类型的所有合法动作的相关牌集合。
	 */
	public Collection<Tile> getLegalActionTiles(
			GameContext context,PlayerLocation location);

	/**
	 * 判断指定动作是否合法。
	 */
	public boolean isLegalAction(GameContext context, PlayerLocation location,
			Action action);

	/**
	 * 执行指定动作。
	 * 
	 * @throws IllegalActionException
	 *             动作非法
	 */
	public void doAction(GameContext context, PlayerLocation location,
			Action action) throws IllegalActionException;

	public Class<? extends ActionType> getRealTypeClass();

	boolean matchBy(ActionType testType);

}
