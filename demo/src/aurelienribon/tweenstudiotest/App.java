package aurelienribon.tweenstudiotest;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenstudio.TweenStudio;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import javax.swing.JOptionPane;

public class App implements ApplicationListener {
	private static final String ANIMATION_1 = "First animation";
	private static final String ANIMATION_2 = "Second animation";

	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private Sprite[] sprites;
	private TweenManager tweenManager;

	public App() {
		// Tween Engine initialization
		Tween.enablePooling(true);
		Tween.registerAccessor(Sprite.class, new SpriteTweenAccessor());

		// ---------------------------------------------------------------------
		// 0. Tween Studio initialization: this single call ("enableEdition()")
		//    lets you toggle between edition and play modes!
		int res = JOptionPane.showConfirmDialog(null, "Do you want to enable the edition of the animations ?", "Tween Studio", JOptionPane.YES_NO_OPTION);
		if (res == 0) TweenStudio.enableEdition();
		// ---------------------------------------------------------------------
	}

	@Override
	public void create() {
		// Common game creation stuff
		float ratio = (float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight();
		camera = new OrthographicCamera(10, 10/ratio);
		spriteBatch = new SpriteBatch();
		tweenManager = new TweenManager();
		createSprites();

		// ---------------------------------------------------------------------
		// 1. Preloading of the animations. You should always do that in your
		//    initialization code and not while your game is running, for
		//    performance reason.
		TweenStudio.loadAnimation(Gdx.files.internal("data/anim1.timeline").file(), ANIMATION_1);
		TweenStudio.loadAnimation(Gdx.files.internal("data/anim2.timeline").file(), ANIMATION_2);

		// 2. Registration of the editor we want to use for the next animations.
		TweenStudio.registerEditor(LibGdxTweenStudioEditorX.class);

		// 2b. The provided LibGdxTweenStudioEditorX editor needs to be
		//     configurated. Be careful that when edition is disabled, all
		//     editor instances are null.
		LibGdxTweenStudioEditorX editor = TweenStudio.getEditor(LibGdxTweenStudioEditorX.class);
		if (editor != null) editor.setup(camera);

		// 3. Registration of the objects that are part of the next animation.
		//    Their names are required to create timelines from serialized
		//    content.
		TweenStudio.registerTarget(sprites[0], "Logo LibGDX");
		TweenStudio.registerTarget(sprites[1], "Logo Tween Engine");
		TweenStudio.registerTarget(sprites[2], "Logo Tween");
		TweenStudio.registerTarget(sprites[3], "Logo Studio");

		// 4. Creation of the first animation timeline. We also add it to our
		//    TweenManager. In edition mode, it won't be started automatically,
		//    to let you edit it.
		TweenStudio.createTimeline(ANIMATION_1).start(tweenManager);

		// That's all! The following lines let you edit a second animation once
		// you're happy with the first one.
		// ---------------------------------------------------------------------

		TweenStudio.unregisterAllTargets();
		TweenStudio.registerTarget(sprites[4], "Wave 1");
		TweenStudio.registerTarget(sprites[5], "Wave 2");
		TweenStudio.registerTarget(sprites[6], "Wave 3");
		TweenStudio.createTimeline(ANIMATION_2).start(tweenManager);
	}

	private void createSprites() {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/pack"));

		sprites = new Sprite[7];

		sprites[0] = atlas.createSprite("logoLibgdx");
		sprites[1] = atlas.createSprite("logoTweenEngine");
		sprites[2] = atlas.createSprite("logoTween");
		sprites[3] = atlas.createSprite("logoStudio");
		sprites[4] = atlas.createSprite("wave");
		sprites[5] = atlas.createSprite("wave");
		sprites[6] = atlas.createSprite("wave");

		for (int i=0; i<sprites.length; i++) {
			float ratio = sprites[i].getWidth() / sprites[i].getHeight();
			sprites[i].setSize(5, 5/ratio);
			sprites[i].setOrigin(sprites[i].getWidth()/2, sprites[i].getHeight()/2);
		}
	}

	@Override
	public void render() {
		int delta = (int) (Gdx.graphics.getDeltaTime() * 1000);
		tweenManager.update(delta);

		// ---------------------------------------------------------------------
		// 5. You need to update the studio periodically (only required with
		//    some editors, like LibGdxTweenStudioEditorX). This won't do
		//    anything in play mode.
		TweenStudio.update(delta);
		// ---------------------------------------------------------------------

		GL10 gl = Gdx.gl10;
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		for (int i=0; i<sprites.length; i++)
			sprites[i].draw(spriteBatch);
		spriteBatch.end();

		// ---------------------------------------------------------------------
		// 6. Some editors (like LibGdxTweenStudioEditorX) render something in
		//    overlay of your game, so you need to add this line too. Again,
		//    this call doesn't cost anything in play mode.
		TweenStudio.render();
		// ---------------------------------------------------------------------
	}

	@Override public void resize(int w, int h) {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void dispose() {}
}
