package tetris;

import processing.core.PApplet;

public class ProcessingPanel extends PApplet {
	int block_size = 15; // ブロックのマス一つの大きさ、もしくは倍率
	int block_distance = block_size + 4; // ブロックの距離感

	// Fieldクラス
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	class Field {

		int x_size = 10 + 2, y_size = 20 + 1; // 縦、横のサイズ

		int[][] field = new int[y_size][x_size];

		int score, line, level;// スコア、ライン、レベル

		Field() {
			format();
		}

		// 初期化のメソッド
		void format() {
			for (int i = 0; i < y_size; i++) {
				for (int j = 0; j < x_size; j++) {
					// 左右の壁,底の壁
					if (j == 0 || j == x_size - 1 || i == y_size - 1)
						field[i][j] = 1;

					else
						field[i][j] = 0;
				}
			}
		}

		// 表示のメソッド
		void display() {

			for (int i = 0; i < y_size; i++) {
				for (int j = 0; j < x_size; j++) {

					// ブロックごとに色分けしている
					if (field[i][j] == 1) {
						stroke(0);
						specular(235, 237, 237);
					} else if (field[i][j] == 2) {
						stroke(0);
						specular(139, 215, 255);
					} else if (field[i][j] == 3) {
						stroke(0);
						specular(233, 255, 5);
					} else if (field[i][j] == 4) {
						stroke(0);
						specular(127, 252, 61);
					} else if (field[i][j] == 5) {
						stroke(0);
						specular(252, 18, 18);
					} else if (field[i][j] == 6) {
						stroke(0);
						specular(3, 0, 250);
					} else if (field[i][j] == 7) {
						stroke(0);
						specular(240, 144, 0);
					} else if (field[i][j] == 8) {
						stroke(0);
						specular(255, 3, 230);
					}
					// ブロックがある場合のみ描画
					if (field[i][j] >= 1) {
						pushMatrix();// 座標の保存
						translate(j * block_distance - halfX, i
								* block_distance - halfY, 0);
						box(block_size, block_size, block_size);
						popMatrix();// 座標の出力
					}
				}
			}
		}

		// 動けるか判断するメソッド、真か偽を返す
		boolean ableMove(int nextx, int nexty, int block[][]) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					// ブロックがある場合のみ調べる
					if (block[i][j] >= 1) {
						// 配列の範囲外にアクセスする場合は偽
						if (nexty + i < 0 || y_size < nexty + i
								|| nextx + j < 0 || x_size < nextx + j) {
							return false;
						}
						// すでにブロックが埋められている場合は偽
						if (field[nexty + i][nextx + j] >= 1) {
							return false;
						}
					}
				}
			}
			return true;
		}

		// ブロックで埋める
		void blocked(int x, int y, int[][] block) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (block[i][j] >= 1) {
						field[y + i][x + j] = block[i][j];
					}
				}
			}
		}

		// 揃った行の削除とスコアの計算と表示
		void deleteAndScore() {

			int tempLine = 0;// 一度に何行消すか

			for (int i = 0; i < y_size - 1; i++) {
				int count = 0;
				for (int j = 1; j < x_size - 1; j++) {
					if (field[i][j] >= 1)
						count++;
				}
				if (count == x_size - 2) {
					line++;
					tempLine++;
					// その行を消去
					for (int j = 1; j < x_size - 1; j++) {
						field[i][j] = 0;
					}

					// 上を落とす
					for (int tempy = i; tempy > 0; tempy--) {
						for (int tempx = 1; tempx < x_size - 1; tempx++) {
							field[tempy][tempx] = field[tempy - 1][tempx];
						}
					}
				}
			}

			// 一度に消した行によってのスコアの計算
			if (tempLine == 1)
				score += 100;
			else if (tempLine == 2)
				score += 200;
			else if (tempLine == 3)
				score += 300;
			else if (tempLine == 4)
				score += 1200;

			level = (line / 5) + 1;// ラインが５行増えるごとにレベルの増加

			// スコアの表示
			specular(0, 100, 255);
			textAlign(CENTER);
			textSize(block_size);
			fill(255);
			text("SCORE\n" + score, (x_size / 2 + 3) * block_distance,
					1 * block_distance, 0);
			text("LINE\n" + line, (x_size / 2 + 3) * block_distance,
					3 * block_distance, 0);
			text("LEVEL\n" + level, (x_size / 2 + 3) * block_distance,
					5 * block_distance, 0);
		}

		// ゲームオーバーの判断のメソッド
		boolean gameover() {
			// 底から一番上までブロックが積み上がっているか判断
			for (int i = 0; i < y_size - 1; i++) {
				for (int j = 1; j < x_size - 1; j++) {
					// ブロックが一つでもあればfor文を一つ抜ける
					if (field[i][j] >= 1)
						break;
					// 最後の列までブロックがなかったら積み上がっていないので偽
					if (j == x_size - 2)
						return false;
				}
			}
			// 最後の行まで行けたなら真
			return true;
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//

	// Blockクラス
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	class Block {
		int bx_size, by_size;// ブロック
		int[][] block;// 各ブロックの配列
		int blockColor;// ブロックの色

		Block() {

			bx_size = 4;
			by_size = 4;// ブロックの枠を4×4で取る
			block = new int[by_size][bx_size];

			format();

		}

		// ブロックの初期化
		void format() {
			for (int i = 0; i < by_size; i++) {
				for (int j = 0; j < bx_size; j++) {
					block[i][j] = 0;
				}
			}
		}

		// 表示するメソッド
		void display(int nowx, int nowy) {

			specular(blockColor);
			for (int i = 0; i < by_size; i++) {
				for (int j = 0; j < bx_size; j++) {
					if (block[i][j] >= 1) {
						pushMatrix();
						translate((nowx + j) * block_distance - halfX,
								(nowy + i) * block_distance - halfY, 0);
						box(block_size, block_size, block_size);
						popMatrix();
					}
				}
			}
		}

		// ブロックを回転するメソッド
		void turn() {
			int[][] turnBlock = new int[by_size][bx_size];

			for (int i = 0; i < by_size; i++) {
				for (int j = 0; j < bx_size; j++) {

					if (3 - j >= 0)// 配列外にアクセスしないための条件
						turnBlock[i][j] = block[3 - j][i];
				}
			}
			// ableMoveメソッドを使って回れるか判断
			if (field.ableMove(x, y, turnBlock))
				block = turnBlock;
		}

		// 次のブロックの表示
		void nextDisplay(Field field, int number) {
			specular(0, 100, 255);
			textAlign(CENTER);
			textSize(block_size);
			text("NEXT", (field.x_size / 2 + 3) * block_distance,
					-(field.y_size / 2) * block_distance, 0);
			display(field.x_size + 1, 1 + 4 * number);
		}

		// ホールドしているブロックの表記
		void holdDisplay(Field field) {
			specular(0, 100, 255);
			textAlign(CENTER);
			textSize(block_size);
			text("HOLD", (field.x_size / 2 + 3) * block_distance,
					-(field.y_size / 2 + 4) * block_distance, 0);
			display(field.x_size + 1, -3);
		}

	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//

	// Blockクラスを継承してそれぞれのブロッククラスを作成
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	class barBlock extends Block {
		barBlock() {
			super();

			blockColor = 255;
			block[0][0] = 2;
			block[0][1] = 2;
			block[0][2] = 2;
			block[0][3] = 2;
		}
	}

	class squreBlock extends Block {
		squreBlock() {
			super();

			blockColor = 5;
			block[0][1] = 3;
			block[0][2] = 3;
			block[1][1] = 3;
			block[1][2] = 3;
		}
	}

	class SBlock extends Block {
		SBlock() {
			super();

			blockColor = 61;
			block[0][2] = 4;
			block[0][3] = 4;
			block[1][1] = 4;
			block[1][2] = 4;
		}
	}

	class ZBlock extends Block {
		ZBlock() {
			super();

			blockColor = 18;
			block[0][1] = 5;
			block[0][2] = 5;
			block[1][2] = 5;
			block[1][3] = 5;
		}
	}

	class JBlock extends Block {
		JBlock() {
			super();

			blockColor = 250;
			block[0][1] = 6;
			block[1][1] = 6;
			block[1][2] = 6;
			block[1][3] = 6;
		}
	}

	class LBlock extends Block {
		LBlock() {
			super();

			blockColor = 0;
			block[0][3] = 7;
			block[1][1] = 7;
			block[1][2] = 7;
			block[1][3] = 7;
		}
	}

	class TBlock extends Block {
		TBlock() {
			super();

			blockColor = 230;
			block[0][2] = 8;
			block[1][1] = 8;
			block[1][2] = 8;
			block[1][3] = 8;
		}
	}

	// スタート文字
	class start extends Block {
		start() {
			super();

			bx_size = 25;
			by_size = 5;
			block = new int[by_size][bx_size];

			blockColor = 210;
			// T
			block[0][0] = 1;
			block[0][1] = 1;
			block[0][2] = 1;
			block[1][1] = 1;
			block[2][1] = 1;
			block[3][1] = 1;
			block[4][1] = 1;
			// E
			block[0][4] = 1;
			block[0][5] = 1;
			block[0][6] = 1;
			block[1][4] = 1;
			block[2][4] = 1;
			block[2][5] = 1;
			block[2][6] = 1;
			block[3][4] = 1;
			block[4][4] = 1;
			block[4][5] = 1;
			block[4][6] = 1;
			// T
			block[0][8] = 1;
			block[0][9] = 1;
			block[0][10] = 1;
			block[1][9] = 1;
			block[2][9] = 1;
			block[3][9] = 1;
			block[4][9] = 1;
			// R
			block[0][12] = 1;
			block[0][13] = 1;
			block[1][12] = 1;
			block[1][14] = 1;
			block[2][12] = 1;
			block[3][12] = 1;
			block[2][13] = 1;
			block[3][14] = 1;
			block[4][12] = 1;
			block[4][14] = 1;
			// I
			block[0][16] = 1;
			block[0][17] = 1;
			block[0][18] = 1;
			block[1][17] = 1;
			block[2][17] = 1;
			block[3][17] = 1;
			block[4][16] = 1;
			block[4][17] = 1;
			block[4][18] = 1;
			// S
			block[0][21] = 1;
			block[0][22] = 1;
			block[1][20] = 1;
			block[2][21] = 1;
			block[3][22] = 1;
			block[4][20] = 1;
			block[4][21] = 1;

		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//

	// 色々と初期化
	// ++++++++++++++++++++++++++++++++++++++++++++++++++//
	Field field = new Field();

	// 現在、次、次の次のブロック
	Block nowBlock, next0_Block, next1_Block, holdBlock;

	// フラグ関係
	boolean gameover = false; // ゲームオーバーのフラグ
	boolean keyState[]; // キーの状態
	boolean stop = false; // 停止のフラグ
	boolean holdFirst = false, holdState = false; // 初めてのホールド、ホールドの状態のフラグ
	boolean startPhase = false;

	// 落ちているブロックの座標
	int x = field.x_size / 2 - 2, y = 0;

	// 落ちるスピード、着地の遊び時間のタイマー
	int down_timer, land_timer;

	// フィールドのx,yの半分の長さ
	int halfX = block_distance * (field.x_size - 1) / 2;
	int halfY = block_distance * (field.y_size - 1) / 2;;

	float gameSpeed; // ゲームの速度

	int rx, ry; // x軸、y軸の回転の値

	// ++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setup() {

		size(2 * field.x_size * block_distance, 2 * field.y_size
				* block_distance, P3D);
		frameRate(60);

		// キーの初期化
		keyState = new boolean[256];

		for (int i = 0; i < 256; ++i)
			keyState[i] = false;

		smooth();

		// ブロックの初期化
		nowBlock = createBlock();
		next0_Block = createBlock();
		next1_Block = createBlock();
		holdBlock = new Block();

		gameSpeed = (frameRate / 2);
	}

	public void draw() {

		background(0);

		// ライト関係
		ambientLight(20, 20, 20); // 環境光
		lightSpecular(80, 80, 100);
		directionalLight(150, 150, 150, -1, 0, -1);
		pointLight(0, 100, 255, mouseX, mouseY, 200);
		spotLight(0, 100, 255, mouseX, mouseY, 200, 0, 0, -1, PI, 2);
		shininess((float) 1.0);

		// 背景載せん
		for (int i = 0; i * block_distance <= 3 * field.x_size * block_distance; i++) {
			if (i * block_distance == width / 2)
				stroke(0, 100, 255, 180);
			else
				stroke(0, 100, 255, 90);

			//line(i * block_distance, 0, 0, i * block_distance, height, 0);
		}
		for (int i = 0; i * block_distance <= 2 * field.y_size * block_distance; i++) {
			if (i * block_distance == height / 2)
				stroke(0, 100, 255, 180);
			else
				stroke(0, 100, 255, 90);

			//line(0, i * block_distance, 0, width, i * block_distance, 0);
		}

		translate(width / 2, height / 2);// 座標を中心にする

		// x軸、y軸の回転
		rotateY(radians(rx));
		rotateX(radians(ry));

		startPhase();

		if (startPhase) {
			// メソッドの実行
			field.display();
			nowBlock.display(x, y);
			next0_Block.nextDisplay(field, 0);
			next1_Block.nextDisplay(field, 1);
			holdBlock.holdDisplay(field);
			field.deleteAndScore();

			normalPhase();

			pause();

			gameOver();

		}

	}

	// キー関係
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void keyPressed() {
		// 同時入力のための
		if (0 <= key && key < 256) {
			keyState[key] = true;
			startPhase = true;
		} else if (0 <= keyCode && keyCode < 256) {
			keyState[keyCode] = true;
			startPhase = true;
		}

		// ホールド
		if (keyState['h'] == true || keyState['H'] == true) {
			// 最初のホールド
			if (!holdFirst) {
				holdBlock = nowBlock;
				nowBlock = next0_Block;
				next0_Block = next1_Block;
				next1_Block = createBlock();

				x = field.x_size / 2 - 2;
				y = 0;

				holdFirst = true;
				holdState = !holdState;
			}
			// 2回目以降
			else {
				if (!holdState) {
					Block tempBlock = nowBlock; // 一時置き場
					nowBlock = holdBlock;
					holdBlock = tempBlock;

					x = field.x_size / 2 - 2;
					y = 0;

					holdState = !holdState;

				}
			}
		}

		// 移動
		if (key == CODED) {
			if (keyState[UP] == true && !stop) {

				nowBlock.turn();

				land_timer = 0;// 移動したなら遊び時間をリセット
			}
			if (keyState[LEFT] == true && !stop) {
				// 移動できるか判断
				if (field.ableMove(x - 1, y, nowBlock.block)) {
					x -= 1;
				}
				land_timer = 0;

			}
			if (keyState[RIGHT] == true && !stop) {

				if (field.ableMove(x + 1, y, nowBlock.block)) {
					x += 1;
				}
				land_timer = 0;
			}
			if (keyState[DOWN] == true && !stop) {
				if (field.ableMove(x, y + 1, nowBlock.block)) {
					y += 1;
					field.score++;
				}
			}
		}

		// 回転
		if (keyState['W'] == true || keyState['w'] == true) {
			ry++;
		}
		if (keyState['A'] == true || keyState['a'] == true) {
			rx--;
		}
		if (keyState['D'] == true || keyState['d'] == true) {
			rx++;
		}
		if (keyState['S'] == true || keyState['s'] == true) {
			ry--;
		}
		if (keyState[' '] == true) {
			if (stop == false) {
				stop = true;
			} else {
				stop = false;
			}
			startPhase = true;
		}

	}

	// 状態のリセット
	public void keyReleased() {
		if (0 <= key && key < 256) {
			keyState[key] = false;
		} else if (0 <= keyCode && keyCode < 256) {
			keyState[keyCode] = false;
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++//

	// draw関数の中の処理の関数化
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	// ブロックの作成関数
	Block createBlock() {
		int type = (int) random(7);

		switch (type) {
		case 0:
			return new barBlock();
		case 1:
			return new squreBlock();
		case 2:
			return new SBlock();
		case 3:
			return new ZBlock();
		case 4:
			return new JBlock();
		case 5:
			return new LBlock();
		case 6:
			return new TBlock();
		}
		return null;
	}

	// 通常時
	void normalPhase() {
		if (!gameover && !stop) {

			// 下に落ちれる場合
			if (field.ableMove(x, y + 1, nowBlock.block)
					&& down_timer >= (frameRate / 2)) {
				y++;
				down_timer = 0;// 落ちる時間のリセット
			}

			// 落ちれない場合
			else if (!field.ableMove(x, y + 1, nowBlock.block)) {

				// 着地してから0.5秒たったら
				if (land_timer > (frameRate / 2)) {
					field.blocked(x, y, nowBlock.block);// ブロックを埋める

					// ブロックの座標を初期化
					x = field.x_size / 2 - 2;
					y = 0;

					// 各ブロックの入替
					nowBlock = next0_Block;
					next0_Block = next1_Block;
					next1_Block = createBlock();
				}

				// ホールドの状態のリセット
				if (holdState)
					holdState = !holdState;

				land_timer++;// 着地時間の増加
			}
			down_timer++;
		}
	}

	// pause時
	public void pause() {
		if (!gameover && stop) {
			textAlign(CENTER);
			textSize(block_size * 6);
			fill(0, 0, 120);
			text("PAUSE", 0, 0, 70);

		}
	}

	// ゲームオーバー
	public void gameOver() {
		if (field.gameover()) {

			textAlign(CENTER);
			textSize(block_size * 6);
			fill(0, 0, 120);
			text("GAME OVER", 0, 0, 70);
			gameover = true;

		}
	}

	// スタート画面
	public void startPhase() {

		if (!startPhase) {
			Block s = new start();
			s.display(-6, 0);

			textAlign(CENTER);
			textSize(block_size * 2);
			specular(222, 227, 0);
			text("Press any key", 0, 0, 0);

			textAlign(CENTER);
			textSize(block_size);
			specular(0, 100, 255);
			text("\n\nManual\n\nMOVE\nUP:Turn\nLEFT:Move left\nRIGHT:Move right\nDOWN:Move down\n\nHOLD\nh:HOLD block\n\nROTATE\na,d:Rotate the wholes's X-axis\nw,s:Rotate the wholes's Y-axis",
					0, 0, 0);

		}
	}

}
