import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

class Kaisen_02 extends JFrame implements KeyListener {
	Image[] img = new Image[3];// 画像の定義用[]個作成
	boolean key_t[] = { false, false, false, false, false }; // UP, RIGHT, DOWN,
																// LEFT，SPACE
	final int MAPMAX_X = 5, MAPMAX_Y = 5;// 海戦マップの横×縦のマス数
	final int MAPMIN_X = 100, MAPMIN_Y = 100;// 海戦マップの左上の座標
	int phase = 0;// 艦の配置・可動・非可動にかかわる値
	Point pos = new Point(MAPMIN_X, MAPMIN_Y);// カーソルの座標
	Point posLC = new Point(MAPMIN_X, MAPMIN_Y);// 軽巡洋艦の艦首の座標
	Dimension size;// パネルのサイズ定義
	Image back;// 背景画像の定義
	Graphics buffer;// バッファの定義

	// Main
	public static void main(String args[]) throws IOException {
		new Kaisen_02();
	}

	// Constructor
	public Kaisen_02() throws IOException {
		super("海戦ゲーム");// スーパークラスであるJFrameのコンストラクタを呼び出す "フレームタイトル"
		img[0] = getToolkit().getImage("src/Select.png");// カーソルの画像
		img[1] = getToolkit().getImage("src/Sea.png");// 海チップの画像
		img[2] = getToolkit().getImage("src/LC.png");// 軽巡洋艦の画像
		addKeyListener(this);// キー操作のインターフェースを呼び出す
		new Thread(new ThreadClass()).start();// Threadを開始する
		setDefaultCloseOperation(EXIT_ON_CLOSE);// ウィンドウの閉じるボタンを押すとプログラムが終了するようにする
		setSize(800, 600);// Dimension(ウィンドウ？）のサイズセットする
		MainPanel p = new MainPanel();
		getContentPane().add(p);
		setVisible(true);// ウィンドウを描画する
		// getContentPane().setBackground(Color.WHITE);

		// size = getSize();// setSizeで設定したサイズをgetsizeで呼び出し，sizeと定義する
		// back = createImage(size.width, size.height);//
		// sizeの中のwidth（幅）height（高さ）を使い，backイメージを作成
		// if (back == null)// 背景が作成されなかったらエラー分を表示する
		// System.err.print("createImage Error");
	}

	// Runnable Class
	class ThreadClass implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					if (action())// キー入力されてカーソル移動が行われた場合
						repaint();// paint()の再実行

					Thread.sleep(100);// スレッドスリープによる画面の切り替えタイミングの決定
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Paint Method

	public class MainPanel extends JPanel {

		public MainPanel() throws IOException {
			setLayout(null);
			setBounds(0, 0, 800, 600);
			setBackground(Color.BLACK);
			// add(makeSubPanel());
		}

		// Paintより手前に表示されるのでpaintComponent中に移動してみた
		/*
		 * public JPanel makeSubPanel() { JPanel sp = new JPanel();
		 * sp.setBackground(Color.BLUE);// 会話ウインドウの色設定 sp.setBounds(0, 450, 784,
		 * 100);// 会話ウィンドウの描画 sp.setBorder(new LineBorder(Color.WHITE, 5,
		 * false)); return sp; }
		 */

		@Override
		// java.awt.Window内のpaint()を上書き
		public void paintComponent(Graphics g) {// gのフィールドはここに書いてある
			super.paintComponent(g);

			// if (back == null)
			// return;

			// buffer = back.getGraphics();//
			// （back）createImageクラスの中にあるgetGraphicsメソッドを呼び出している

			// JPanel buffer = new JPanel();//
			// （back）createImageクラスの中にあるgetGraphicsメソッドを呼び出している
			// buffer.setBounds(0, 0,816, 600);

			// if (buffer == null)
			// return;

			Font font = new Font(Font.MONOSPACED, Font.BOLD, 20); // フォントの設定・定義
			g.setFont(font);// フォントをセットする
			// g.setColor(Color.BLUE);
			// size = getSize();// サイズの呼び出し

			// g.setColor(getBackground());//
			// createImage()->getGraphics()->setColor(getBackgraound())
			// 背景をとってきて色を付けた画像を情報を取得イメージを作成する
			// g.fillRect(0, 0, 800, 600);// 画面の更新

			drawSea(g);// 海チップの描画

			mapEnd();// カーソルマップ端移動処理
			JLabel jl = new JLabel();

			jl.setBounds(13, 450, 480, 100);
			jl.setFont(font);
			jl.setHorizontalTextPosition(JLabel.CENTER);
			jl.setVerticalAlignment(JLabel.TOP);
			jl.setForeground(Color.WHITE);
			jl.setText("<HTML>テスト<br>配置する艦を選択してください");// 表示できたけど場合わけできん・・・
			add(jl);

			JPanel sp = new JPanel();
			sp.setBackground(Color.BLUE);// 会話ウインドウの色設定
			sp.setBounds(0, 450, 784, 100);// 会話ウィンドウの描画
			sp.setBorder(new LineBorder(Color.WHITE, 5, false));

			g.setColor(Color.WHITE);
			if (phase == 0) {// TODO 配置する艦を選択（今後実装）
				g.drawString("配置する艦を選択してください", 18, 530);
			} else if (phase == 1) {// 艦の召喚　可動状態
				posLC.x = pos.x;// posLC =pos;はオブジェクトのコピーではなく参照コピーとなるため×
				posLC.y = pos.y;

				g.drawImage(img[2], pos.x, pos.y, this);

			} else if (phase == 2) {// 艦の投錨　固定状態へ
				g.drawImage(img[2], posLC.x, posLC.y, this);

			} else if (phase >= 3 && posLC.x == pos.x && posLC.y == pos.y) {// 艦の抜錨　可動状態へ
				g.drawImage(img[2], pos.x, pos.y, this);
				phase = 1;

			} else if (phase >= 3) {// 艦の先端を選べていない　可動状態にするには船首を選ぶ
				g.drawImage(img[2], posLC.x, posLC.y, this);
				jl.setText("艦の先端を選べていません。可動状態にするには船首を選んでください。");
				add(jl);//機能しない？
				phase = 2;
			}
			add(sp);// 会話ウィンドウの追加
			System.out.println(posLC + " " + pos + " " + phase);// デバック
			g.drawImage(img[0], pos.x, pos.y, this);// カーソルの描画
		}

		public void drawSea(Graphics g) {// 海マップの配置 TODO　合成した画像を作り，バッファに貼るようにする
			for (int ii = 0; ii < MAPMAX_X; ii++) {
				for (int jj = 0; jj < MAPMAX_Y; jj++) {
					g.drawImage(img[1], MAPMIN_X + 30 * ii, MAPMIN_Y + 30 * jj,
							this);
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

	}

	// カーソルの移動
	public boolean action() {
		if (key_t[0] == true) {// UP
			pos.y -= 30;
			return true;
		}
		if (key_t[1] == true) { // RIGHT
			pos.x += 30;
			return true;
		}
		if (key_t[2] == true) { // DOWN
			pos.y += 30;
			return true;
		}
		if (key_t[3] == true) {// LEFT
			pos.x -= 30;
			return true;
		}
		if (key_t[4] == true) {// SPACE
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
			key_t[0] = true;
			break;
		case KeyEvent.VK_RIGHT:
			key_t[1] = true;
			break;
		case KeyEvent.VK_DOWN:
			key_t[2] = true;
			break;
		case KeyEvent.VK_LEFT:
			key_t[3] = true;
			break;
		case KeyEvent.VK_SPACE:
			key_t[4] = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			key_t[0] = false;
			break;
		case KeyEvent.VK_RIGHT:
			key_t[1] = false;
			break;
		case KeyEvent.VK_DOWN:
			key_t[2] = false;
			break;
		case KeyEvent.VK_LEFT:
			key_t[3] = false;
			break;
		case KeyEvent.VK_SPACE:
			key_t[4] = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
