import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

class Kaisen_02 extends JFrame implements KeyListener {
	Image[] img = new Image[3];// 画像の定義用[]個作成
	int key_t[] = { 0, 0, 0, 0, 0 }; // UP, RIGHT, DOWN, LEFT，SPACE
	final int MAPMAX_X = 5, MAPMAX_Y = 5;// 海戦マップの横×縦のマス数
	final int MAPMIN_X = 100, MAPMIN_Y = 100;// 海戦マップの左上の座標
	int phase = 0;// 艦の配置・可動・非可動にかかわる値
	Point pos = new Point(MAPMIN_X, MAPMIN_Y);// カーソルの座標
	Point posLC = new Point(MAPMIN_X, MAPMIN_Y);// 軽巡洋艦の艦首の座標
	Dimension size;// パネルのサイズ定義
	Image back;// 背景画像の定義
	Graphics buffer;// バッファの定義

	// Main
	public static void main(String args[]) {
		new Kaisen_02();
	}

	// Constructor
	public Kaisen_02() {
		super("海戦ゲーム");// JFrame() 初期状態が不可視である、新しい Frameを構築します "フレームタイトル"
		img[0] = getToolkit().getImage("src/Select.png");// カーソルの画像
		img[1] = getToolkit().getImage("src/Sea.png");// 海チップの画像
		img[2] = getToolkit().getImage("src/LC.png");// 軽巡洋艦の画像
		addKeyListener(this);// キー操作のインターフェースを呼び出す
		ThreadClass threadcls = new ThreadClass();// ThreadClass()のインスタンスを生成する
		Thread thread = new Thread(threadcls);// Thread()のインスタンスを生成する
		thread.start();// Threadを開始する
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// ウィンドウの閉じるボタンを押すとプログラムが終了するようにする
		setSize(640, 480);// Dimension(ウィンドウ？）のサイズセットする
		setBackground(Color.BLACK);// ウィンドウの背景色を設定する
		setVisible(true);// ウィンドウを描画する
		size = getSize();// setSizeで設定したサイズをgetsizeで呼び出し，sizeと定義する
		back = createImage(size.width, size.height);// sizeの中のwidth（幅）height（高さ）を使い，backイメージを作成
		if (back == null)// 背景が作成されなかったらエラー分を表示する
			System.err.print("createImage Error");
	}

	// Runnable Class
	class ThreadClass implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					if (action())// キー入力されてカーソル移動が行われた場合
						repaint();// paint()の再実行
					Thread.sleep(150);// スレッドスリープによる画面の切り替えタイミングの決定
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Paint Method
	@Override
	// java.awt.Window内のpaint()を上書き？どこで読み込んだんだ？　おそらくSwingに内包されている？
	public void paint(Graphics g) {
		if (back == null)
			return;

		buffer = back.getGraphics();// （back）createImageクラスの中にあるgetGraphicsメソッドを呼び出している
		if (buffer == null)
			return;

		//size = getSize();// サイズの呼び出し　再び？メソッド内だから？いらない？
		buffer.setColor(getBackground());// createImage()->getGraphics()->setColor(getBackgraound())
											// 背景をとってきて色を付けた画像を情報を取得イメージを作成する
		buffer.fillRect(0, 0, size.width, size.height);// 指定された短形を塗りつぶす　画面の更新？
		drawSea();
		mapEnd();

		if (phase == 0) {

		} else if (phase == 1) {// 艦の召喚　可動状態
			posLC.x = pos.x;// posLC =pos;にするとオブジェクトのコピーじゃなく，オブジェクトの参照コピーであるため×
			posLC.y = pos.y;
			buffer.drawImage(img[2], pos.x, pos.y, this);

		} else if (phase == 2) {// 艦の投錨　固定状態へ
			buffer.drawImage(img[2], posLC.x, posLC.y, this);

		} else if (phase >= 3 && posLC.x == pos.x && posLC.y == pos.y) {// 艦の抜錨　可動状態へ
			buffer.drawImage(img[2], pos.x, pos.y, this);
			phase = 1;

		} else if (phase >= 3) {// 艦の先端を選べていない　可動状態にするには船首を選ぶ
			// JLabel jl=new JLabel();//TODO Jpanelをバッファに追加したい
			buffer.drawImage(img[2], posLC.x, posLC.y, this);
			buffer.drawString("艦の先端を選べていません。可動状態にするには船首を選んでください", 200, 20);
			phase = 2;
		}
		System.out.println(posLC + " " + pos + " " + phase);// デバック
		buffer.drawImage(img[0], pos.x, pos.y, this);
		
		
		g.drawImage(back, 0, 0, this);// gのクラス？どこ？画像全体をクラスとおいている？？bufferのこと？
	}

	public void drawSea() {// 海マップの配置 TODO　合成した画像を作り，バッファに貼るようにする
		for (int ii = 0; ii < MAPMAX_X; ii++) {
			for (int jj = 0; jj < MAPMAX_Y; jj++) {
				buffer.drawImage(img[1], MAPMIN_X + 30 * ii,
						MAPMIN_Y + 30 * jj, this);
			}
		}
	}

	// カーソルが海マップをはみ出したときに反対側から戻ってくるような処理
	public void mapEnd() {
		if (pos.x < MAPMIN_X) {
			pos.x += 30 * MAPMAX_X;
		} else if (pos.x > MAPMIN_X + 30 * (MAPMAX_X - 1)) {
			pos.x -= 30 * MAPMAX_X;
		} else if (pos.y < MAPMIN_Y) {
			pos.y += 30 * MAPMAX_Y;
		} else if (pos.y > MAPMIN_Y + 30 * (MAPMAX_Y - 1)) {
			pos.y -= 30 * MAPMAX_Y;
		}
	}

	// カーソルの移動
	public boolean action() {
		if (key_t[0] == 1) {// UP
			pos.y -= 30;
			return true;
		}
		if (key_t[1] == 1) { // RIGHT
			pos.x += 30;
			return true;
		}
		if (key_t[2] == 1) { // DOWN
			pos.y += 30;
			return true;
		}
		if (key_t[3] == 1) {// LEFT
			pos.x -= 30;
			return true;
		}
		if (key_t[4] == 1) {// SPACE
			phase += 1;
			return true;
		}
		return false;
	}

	// KeyEvent Listener
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			key_t[0] = 1;
			break;
		case KeyEvent.VK_RIGHT:
			key_t[1] = 1;
			break;
		case KeyEvent.VK_DOWN:
			key_t[2] = 1;
			break;
		case KeyEvent.VK_LEFT:
			key_t[3] = 1;
			break;
		case KeyEvent.VK_SPACE:
			key_t[4] = 1;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			key_t[0] = 0;
			break;
		case KeyEvent.VK_RIGHT:
			key_t[1] = 0;
			break;
		case KeyEvent.VK_DOWN:
			key_t[2] = 0;
			break;
		case KeyEvent.VK_LEFT:
			key_t[3] = 0;
			break;
		case KeyEvent.VK_SPACE:
			key_t[4] = 0;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
