/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action.sprite;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.LTrans;
import loon.action.ActionBind;
import loon.action.collision.CollisionHelper;
import loon.action.map.Field2D;
import loon.canvas.LColor;
import loon.component.layout.BoxSize;
import loon.geom.Affine2f;
import loon.geom.Point;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.opengl.TextureUtils;
import loon.utils.MathUtils;
import loon.utils.res.MovieSpriteSheet;

public class Sprite extends LObject implements ActionBind, ISprite, LTrans,
		BoxSize {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982110847888726016L;

	// 默认每帧刷新时间
	final static private long defaultTimer = 150;

	// 是否可见
	private boolean visible = true;

	// 精灵名称
	private String spriteName;

	// 精灵图片
	private LTexture image;

	// 动画
	private Animation animation = new Animation();

	private int transform;

	private float scaleX = 1, scaleY = 1;

	private int maxFrame;

	/**
	 * 默认构造函数
	 * 
	 */
	public Sprite() {
		this(0, 0);
	}

	/**
	 * 以下参数分别为 坐标x,坐标y
	 * 
	 * @param x
	 * @param y
	 */
	public Sprite(float x, float y) {
		this("Sprite" + System.currentTimeMillis(), x, y);
	}

	/**
	 * 以下参数分别为 精灵名,坐标x,坐标y
	 * 
	 * @param spriteName
	 * @param x
	 * @param y
	 */
	private Sprite(String spriteName, float x, float y) {
		this.setLocation(x, y);
		this.spriteName = spriteName;
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int row, int col) {
		this(fileName, -1, 0, 0, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int row, int col, long timer) {
		this(fileName, -1, 0, 0, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, float x, float y, int row, int col) {
		this(fileName, x, y, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	private Sprite(String fileName, float x, float y, int row, int col,
			long timer) {
		this(fileName, -1, x, y, row, col, timer);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 */
	public Sprite(String fileName, int maxFrame, float x, float y, int row,
			int col) {
		this(fileName, maxFrame, x, y, row, col, defaultTimer);
	}

	/**
	 * 以下参数分别为 取材文件,最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String fileName, int maxFrame, float x, float y, int row,
			int col, long timer) {
		this("Sprite" + System.currentTimeMillis(), fileName, maxFrame, x, y,
				row, col, timer);
	}

	/**
	 * 以下参数分别为 精灵名，取材文件，最大分解桢数,坐标x,坐标y,每行取材宽度,每列取材长度,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param fileName
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param row
	 * @param col
	 * @param timer
	 */
	public Sprite(String spriteName, String fileName, int maxFrame, float x,
			float y, int row, int col, long timer) {
		this(spriteName, TextureUtils.getSplitTextures(fileName, row, col),
				maxFrame, x, y, timer);
	}

	/**
	 * 注入指定图片
	 * 
	 * @param fileName
	 */
	public Sprite(String fileName) {
		this(LTextures.loadTexture(fileName));
	}

	/**
	 * 注入指定图片
	 * 
	 * @param images
	 */
	public Sprite(final LTexture img) {
		this(new LTexture[] { img }, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组
	 * 
	 * @param images
	 */
	public Sprite(LTexture[] images) {
		this(images, 0, 0);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y
	 * 
	 * @param images
	 * @param x
	 * @param y
	 */
	public Sprite(LTexture[] images, float x, float y) {
		this(images, x, y, defaultTimer);
	}

	/**
	 * 以下参数分别为 图像数组,平均每桢显示时间
	 * 
	 * @param images
	 * @param timer
	 */
	public Sprite(LTexture[] images, long timer) {
		this(images, -1, 0, 0, defaultTimer);
	}

	/**
	 * 以下参数分别为 图像数组,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param images
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture[] images, float x, float y, long timer) {
		this(images, -1, x, y, timer);
	}

	/**
	 * 以下参数分别为 图像数组,最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(LTexture[] images, int maxFrame, float x, float y, long timer) {
		this("Sprite" + System.currentTimeMillis(), images, maxFrame, x, y,
				timer);
	}

	/**
	 * 以下参数分别为 精灵名，图像数组，最大分解桢数,坐标x,坐标y,平均每桢显示时间
	 * 
	 * @param spriteName
	 * @param images
	 * @param maxFrame
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(String spriteName, LTexture[] images, int maxFrame, float x,
			float y, long timer) {
		this.setLocation(x, y);
		this.spriteName = spriteName;
		this.setAnimation(animation, images, maxFrame, timer);
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
	}

	/**
	 * 以资源SpriteSheet构建精灵
	 * 
	 * @param sheet
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(MovieSpriteSheet sheet, float x, float y, long timer) {
		this("Sprite" + System.currentTimeMillis(), sheet, x, y, timer);
	}

	/**
	 * 以资源SpriteSheet构建精灵
	 * 
	 * @param spriteName
	 * @param sheet
	 * @param x
	 * @param y
	 * @param timer
	 */
	public Sprite(String spriteName, MovieSpriteSheet sheet, float x, float y,
			long timer) {
		this.setLocation(x, y);
		this.spriteName = spriteName;
		LTexture[] texs = sheet.getTextures();
		this.setAnimation(animation, texs, texs.length, timer);
		this.visible = true;
		this.transform = LTrans.TRANS_NONE;
	}

	/**
	 * 是否在播放动画
	 * 
	 * @param running
	 */
	public void setRunning(boolean running) {
		animation.setRunning(running);
	}

	/**
	 * 返回当前总桢数
	 * 
	 * @return
	 */
	public int getTotalFrames() {
		return animation.getTotalFrames();
	}

	/**
	 * 设定当前帧
	 * 
	 * @param index
	 */
	public void setCurrentFrameIndex(int index) {
		animation.setCurrentFrameIndex(index);
	}

	/**
	 * 返回当前桢索引
	 * 
	 * @return
	 */
	public int getCurrentFrameIndex() {
		return animation.getCurrentFrameIndex();
	}

	/**
	 * 获得当前精灵的窗体居中横坐标
	 * 
	 * @param x
	 * @return
	 */
	public int centerX(int x) {
		return centerX(this, x);
	}

	/**
	 * 获得指定精灵的窗体居中横坐标
	 * 
	 * @param sprite
	 * @param x
	 * @return
	 */
	public static int centerX(Sprite sprite, int x) {
		int newX = (int) (x - (sprite.getWidth() / 2));
		if (newX + sprite.getWidth() >= LSystem.viewSize.getWidth()) {
			return (int) (LSystem.viewSize.getWidth() - sprite.getWidth() - 1);
		}
		if (newX < 0) {
			return x;
		} else {
			return newX;
		}
	}

	/**
	 * 获得当前精灵的窗体居中纵坐标
	 * 
	 * @param y
	 * @return
	 */
	public int centerY(int y) {
		return centerY(this, y);
	}

	/**
	 * 获得指定精灵的窗体居中纵坐标
	 * 
	 * @param sprite
	 * @param y
	 * @return
	 */
	public static int centerY(Sprite sprite, int y) {
		int newY = (int) (y - (sprite.getHeight() / 2));
		if (newY + sprite.getHeight() >= LSystem.viewSize.getHeight()) {
			return (int) (LSystem.viewSize.getHeight() - sprite.getHeight() - 1);
		}
		if (newY < 0) {
			return y;
		} else {
			return newY;
		}
	}

	/**
	 * 插入指定动画
	 * 
	 * @param myAnimation
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	private void setAnimation(Animation myAnimation, LTexture[] images,
			int max, long timer) {
		this.maxFrame = max;
		if (maxFrame != -1) {
			for (int i = 0; i < maxFrame; i++) {
				myAnimation.addFrame(images[i], timer);
			}
		} else {
			for (int i = 0; i < images.length; i++) {
				myAnimation.addFrame(images[i], timer);
			}
		}
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param maxFrame
	 * @param row
	 * @param col
	 * @param timer
	 */
	public void setAnimation(String fileName, int maxFrame, int row, int col,
			long timer) {
		setAnimation(new Animation(),
				TextureUtils.getSplitTextures(fileName, row, col), maxFrame,
				timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param fileName
	 * @param row
	 * @param col
	 * @param timer
	 */
	public void setAnimation(String fileName, int row, int col, long timer) {
		setAnimation(fileName, -1, row, col, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param maxFrame
	 * @param timer
	 */
	public void setAnimation(LTexture[] images, int maxFrame, long timer) {
		setAnimation(new Animation(), images, maxFrame, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param images
	 * @param timer
	 */
	public void setAnimation(LTexture[] images, long timer) {
		setAnimation(new Animation(), images, -1, timer);
	}

	/**
	 * 插入指定动画
	 * 
	 * @param animation
	 */
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * 变更动画
	 */
	public void update(long timer) {
		if (visible) {
			animation.update(timer);
		}
	}

	/**
	 * 变更定位器坐标
	 * 
	 * @param vector
	 */
	public void updateLocation(Vector2f vector) {
		this.setX(MathUtils.round(vector.getX()));
		this.setY(MathUtils.round(vector.getY()));
	}

	public LTexture getImage() {
		return animation.getSpriteImage();
	}

	@Override
	public float getWidth() {
		LTexture si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return (int) (si.width() * scaleX);
	}

	@Override
	public float getHeight() {
		LTexture si = animation.getSpriteImage();
		if (si == null) {
			return -1;
		}
		return (int) (si.height() * scaleY);
	}

	/**
	 * 获得精灵的中间位置
	 * 
	 * @return
	 */
	public Point getMiddlePoint() {
		return new Point(getLocation().x() + getWidth() / 2, getLocation().y()
				+ getHeight() / 2);
	}

	/**
	 * 获得两个精灵的中间距离
	 * 
	 * @param second
	 * @return
	 */
	public float getDistance(Sprite second) {
		return (float) this.getMiddlePoint()
				.distanceTo(second.getMiddlePoint());
	}

	/**
	 * 返回碰撞盒
	 * 
	 * @return
	 */
	public RectBox getCollisionBox() {
		return getRect(getLocation().x(), getLocation().y(), getWidth(),
				getHeight());
	}

	/**
	 * 检查是否与指定精灵位置发生了矩形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToRect(Sprite sprite) {
		return CollisionHelper.isRectToRect(this.getCollisionBox(),
				sprite.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isCircToCirc(Sprite sprite) {
		return CollisionHelper.isCircToCirc(this.getCollisionBox(),
				sprite.getCollisionBox());
	}

	/**
	 * 检查是否与指定精灵位置发生了方形与圆形碰撞
	 * 
	 * @param sprite
	 * @return
	 */
	public boolean isRectToCirc(Sprite sprite) {
		return CollisionHelper.isRectToCirc(this.getCollisionBox(),
				sprite.getCollisionBox());
	}

	private LColor filterColor;

	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (_alpha < 0.01) {
			return;
		}

		if (animation.getCurrentFrameIndex() > maxFrame) {
			animation.reset();
		}
		image = animation.getSpriteImage();

		if (image == null) {
			return;
		}
		float width = image.getWidth();
		float height = image.getHeight();
		float tmp = g.alpha();
		boolean update = !(scaleX == 1f && scaleY == 1f);
		try {
			if (update) {
				g.saveTx();
				Affine2f tx = g.tx();
				final float scaleCenterX = this._location.x + width / 2f;
				final float scaleCenterY = this._location.y + height / 2f;
				tx.translate(scaleCenterX, scaleCenterY);
				tx.preScale(scaleX, scaleY);
				tx.translate(-scaleCenterX, -scaleCenterY);
			}
			if (_alpha > 0 && _alpha < 1) {
				g.setAlpha(_alpha);
			}
			if (filterColor == null) {
				if (LTrans.TRANS_NONE == transform) {
					g.draw(image, this._location.x, this._location.y, width,
							height, _rotation);
				} else {
					g.drawRegion(image, 0, 0, (int) width, (int) height,
							transform, x(), y(), LTrans.TOP | LTrans.LEFT);
				}
			} else {
				if (LTrans.TRANS_NONE == transform) {
					g.draw(image, this._location.x, this._location.y, width,
							height, filterColor, _rotation);
				} else {
					g.drawRegion(image, 0, 0, (int) width, (int) height,
							transform, x(), y(), LTrans.TOP | LTrans.LEFT,
							filterColor);
				}
			}
		} finally {
			g.setAlpha(tmp);
			if (update) {
				g.restoreTx();
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}

	public int getTransform() {
		return transform;
	}

	public void setTransform(int transform) {
		this.transform = transform;
	}

	public LColor getFilterColor() {
		return filterColor;
	}

	public void setFilterColor(LColor filterColor) {
		this.filterColor = filterColor;
	}

	public LTexture getBitmap() {
		return this.image;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	@Override
	public Field2D getField2D() {
		return null;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean inContains(float x, float y, float w, float h) {
		return getCollisionBox().contains(x, y, w, h);
	}

	@Override
	public RectBox getRectBox() {
		return getCollisionBox();
	}

	public void setScale(float s) {
		this.setScale(s, s);
	}

	@Override
	public void setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
	}

	public void setSize(float width, float height) {
		this.scaleX = getWidth() / width;
		this.scaleY = getHeight() / height;
	}

	public int getMaxFrame() {
		return maxFrame;
	}

	public void setMaxFrame(int maxFrame) {
		this.maxFrame = maxFrame;
	}

	@Override
	public void setWidth(float w) {
		this.scaleX = (w / getWidth());
	}

	@Override
	public void setHeight(float h) {
		this.scaleY = (h / getHeight());
	}

	@Override
	public void close() {
		this.visible = false;
		if (image != null) {
			image.close();
		}
		if (animation != null) {
			animation.close();
		}
	}
}
