package com.starshooter.screens;

import java.text.DecimalFormat;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.starshooter.models.EnemyWave;
import com.starshooter.models.Laser;
import com.starshooter.models.StarShip.LaserListener;
import com.starshooter.models.StarShip.UIListener;
import com.starshooter.models.StarVoyager;
import com.starshooter.util.AlgebraUtils;
import com.starshooter.util.FontUtils;
import com.starshooter.util.TextureCache;

public class StarTerrain implements Screen, LaserListener, UIListener {

	private int score = 0;
	
	final float width, height;
	
	//UI STUFF
	private Sprite xIcon;
	
	private final StarField starField;
	private final StarVoyager ship;
	private final SpriteBatch batch;
	private final Array<Laser> friendlies = new Array<Laser>(false, 64);
	private final Array<Laser> foes = new Array<Laser>(false, 32);
	
	private final Array<EnemyWave> enemies = new Array<EnemyWave>();
	
	public StarTerrain(SpriteBatch batch) {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.batch = batch;
		this.starField = new StarField(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.ship = new StarVoyager(this, this);
		this.xIcon = new Sprite(TextureCache.obtain().get("numeralX"));
	}

	@Override
	public void onFire(Laser laser) {
		if (laser.isFriendly()) friendlies.add(laser);
		else foes.add(laser);
	}
	
	@Override
	public void render(float delta) {
		batch.begin();
		
		starField.advance(delta);
		ship.update(delta);
		for (Laser laser: friendlies) laser.update(delta);
		for (Laser laser: foes) laser.update(delta);
		for (EnemyWave enemy: enemies) 
			enemy.update(delta, ship.getX(), ship.getY());
		EnemyWave wave = EnemyWave.update(delta, this, this);
		if (wave != null) enemies.add(wave);
	
		{
			Iterator<EnemyWave> it = enemies.iterator();
			while (it.hasNext()) {
				EnemyWave enemyWave = it.next();
				if (enemyWave.noMoreShips()) {
					it.remove();
				}
			}
		}
		
		starField.render(batch);
		for (Laser laser: friendlies) laser.draw(batch);
		for (Laser laser: foes) laser.draw(batch);
		
		for (EnemyWave enemy: enemies) enemy.draw(batch);
		ship.draw(batch);
		
		{
			Iterator<Laser> it = foes.iterator();
			while (it.hasNext()) {
				Laser foe = it.next();
				if (ship.checkCollision(foe)) {
					it.remove();
					ship.damage(foe.getDamageDealt());
				}
			}
		}
		
		{
			Iterator<Laser> it = friendlies.iterator();
			while (it.hasNext()) {
				Laser friendly = it.next();
				boolean hit = false;
				for (EnemyWave enemyWave: enemies) {
					if (enemyWave.checkCollision(friendly)) {
						hit = true;
						break;
					}
				}
				if (hit) it.remove();
			}
		}
		
		renderUI(batch);
		batch.end();
		
//		ShapeRenderer s = new ShapeRenderer();
//		s.begin(ShapeType.Line);
//		for (EnemyWave enemy: enemies) enemy.debug(s);
//		s.end();
		
	}

	public void renderUI(SpriteBatch batch) {
		DecimalFormat oneDigit = new DecimalFormat("#,##0.0");//format to 1 decimal place
		//lives
		int lives = ship.numLives;
		Sprite lifeIcon = ship.lifeIcon;
		lifeIcon.setPosition(50, height - 53);
		xIcon.setPosition(100, height - 50);
		lifeIcon.draw(batch);
		xIcon.draw(batch);
		FontUtils.draw(batch, "" + lives, 135, height - 33);
		//cannon power
		FontUtils.draw(batch, "AMMO:", width - 270, height - 33);
		FontUtils.drawShadedFont(batch, ship.cannonPower/100f, oneDigit.format(ship.cannonPower) + "%", width - 145, height - 33);
		//score
		FontUtils.draw(batch, "Score: " + score, width - 270, height - 83);
		//health 
		float percentHealth = (ship.getHealth() * 100f) / ship.getMaxHealth();
		FontUtils.draw(batch, "HEALTH: ", 200, height - 33);
		FontUtils.drawShadedFont(batch, percentHealth/100f, oneDigit.format(percentHealth) + "%", 360, height- 33);
	}
	
	@Override
	public void onScoreChange(int addition) {
		score += addition;
	}
	
	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		ship.dispose();
	}

}
