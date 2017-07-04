package ru.kizup.minibox2dgame.model.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class Box2dTargetInputProcessor extends InputAdapter {
	protected Box2dSteeringEntity target;

	public Box2dTargetInputProcessor(Box2dSteeringEntity target) {
		this.target = target;
	}

	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		setTargetPosition(screenX, screenY);
		return true;
	}

	@Override
	public boolean touchDragged (int screenX, int screenY, int pointer) {
		setTargetPosition(screenX, screenY);
		return true;
	}

	protected void setTargetPosition(int screenX, int screenY) {
		Vector2 pos = target.getPosition();
		screenY = Gdx.graphics.getHeight() - screenY;
		pos.x = screenX;
		pos.y = screenY;
		target.getBody().setTransform(pos, target.body.getAngle());
	}
}