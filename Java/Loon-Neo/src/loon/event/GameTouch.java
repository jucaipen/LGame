/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.event;

import loon.geom.Vector2f;

public class GameTouch {

	int type;

	float x, y;

	float dx, dy;

	int button;

	int pointer;

	int id;

	GameTouch() {

	}

	public GameTouch(float x, float y, int pointer, int id) {
		this.set(x, y, pointer, id);
	}

	public void set(float x, float y, int pointer, int id) {
		this.x = x;
		this.y = y;
		this.pointer = pointer;
		this.id = id;
	}

	GameTouch(GameTouch touch) {
		this.type = touch.type;
		this.x = touch.x;
		this.y = touch.y;
		this.button = touch.button;
		this.pointer = touch.pointer;
		this.id = touch.id;
	}

	public void offset(float x, float y) {
		this.x += x;
		this.y += y;
	}

	public void offsetX(float x) {
		this.x += x;
	}

	public void offsetY(float y) {
		this.y += y;
	}

	public boolean equals(GameTouch e) {
		if (e == null) {
			return false;
		}
		if (e == this) {
			return true;
		}
		if (e.type == type && e.x == x && e.y == y && e.button == button
				&& e.pointer == pointer && e.id == id) {
			return true;
		}
		return false;
	}

	public int getButton() {
		return button;
	}

	public int getPointer() {
		return pointer;
	}

	public int getType() {
		return type;
	}

	public int getID() {
		return id;
	}

	public int x() {
		return (int) x;
	}

	public int y() {
		return (int) y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getDX() {
		return dx;
	}

	public float getDY() {
		return dy;
	}

	static boolean isDraging;

	public boolean isDown() {
		return button == SysTouch.TOUCH_DOWN;
	}

	public boolean isUp() {
		return button == SysTouch.TOUCH_UP;
	}

	public boolean isMove() {
		return button == SysTouch.TOUCH_MOVE;
	}

	public boolean isDrag() {
		return isDraging;
	}

	public Vector2f get() {
		return new Vector2f(x, y);
	}
}
