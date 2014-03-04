import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

class Kaisen_02 extends JFrame implements KeyListener {
	Image[] img = new Image[3];// 画像の定義用[]個作成
	// キーのオン・オフ　UP, RIGHT, DOWN, LEFT，SPACE，Z
	boolean key_t[] = { false, false, false, false, false ,false};
	final int MAPMAX_X = 8, MAPMAX_Y = 8;// 海戦マップの横×縦のマス数
	final int MAPMIN_X = 100, MAPMIN_Y = 100;// 海戦マップの左上の座標
	int phase = 0;// 艦の配置・可動・非可動にかかわる値
	Point pos = new Point(MAPMIN_X, MAPMIN_Y);// カーソルの座標
	Point posLC = new Point(MAPMIN_X, MAPMIN_Y);// 軽巡洋艦の艦首の座標
	boolean isShip[] = { false };// 艦を掴んでいるか
	int[] ship_len = { 3 };// 艦の長さ
	boolean quar =false;// 艦のむき縦T，横F

	// Main
	public static void main(String args[]) throws IOException {
		// 安全なJFrameの生成
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new Kaisen_02();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor
	public Kaisen_02() throws IOException {
		super("海戦ゲーム");// スーパークラスであるJFrameのコンストラクタを呼び出す "フレームタイトル"
		img[0] = getToolkit().getImage("resource/Select.png");// カーソルの画像
		img[1] = getToolkit().getImage("resource/Sea.png");// 海チップの画像
		img[2] = getToolkit().getImage("resource/LC.png");// 軽巡洋艦の画像
		addKeyListener(this);// キー操作のインターフェースを呼び出す
		new Thread(new ThreadClass()).start();// Threadを開始する
		setDefaultCloseOperation(EXIT_ON_CLOSE);// ウィンドウの閉じるボタンを押すとプログラムが終了するようにする
		setSize(800, 600);// Dimension(ウィンドウ？）のサイズセットする
		setResizable(false);// ウィンドウリサイズ無効化
		MainPanel p = new MainPanel();
		getContentPane().add(p);
		setVisible(true);// ウィンドウを描画する
	}

	public Image getImageInJar(String filePath) {
		URL url = getClass().getClassLoader().getResource(filePath);
		try {
			if (url != null) {
				return createImage((ImageProducer) url.getContent());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getToolkit().getImage(filePath);
	}


	// Runnable Class
	class ThreadClass implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					if (action())// キー入力されてカーソル移動が行われた場合
						repaint();// paint()の再実行
					Thread.sleep(140);// スレッドスリープによる画面の切り替えタイミングの決定
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Paint Method

	public class MainPanel extends JPanel {
		Font font;
		JLabel jl;
		JPanel sp;

		public MainPanel() throws IOException {
			setLayout(null);
			setBackground(Color.BLACK);

			font = new Font(Font.MONOSPACED, Font.BOLD, 20); // フォントの設定・定義

			// 会話ウィンドウに表示するラベルの作成
			jl = new JLabel();
			jl.setFont(font);
			jl.setHorizontalTextPosition(JLabel.CENTER);
			jl.setVerticalAlignment(JLabel.TOP);
			jl.setForeground(Color.WHITE);
			jl.setText("<HTML>配置する艦を選択してください<br> SPACE：軽巡洋艦");

			// 会話ウィンドウの作成
			sp = new JPanel();
			sp.setBackground(Color.BLUE); // 会話ウインドウの色設定
			sp.setBounds(0, 422, 795, 150); // 会話ウィンドウの描画
			sp.setBorder(new LineBorder(Color.WHITE, 5, false));
			sp.setLayout(new BorderLayout());
			sp.add(jl);// ラベルを会話ウィンドウに追加
			add(sp); // 会話ウィンドウをフレームに追加
		}

		@Override
		public void paintComponent(Graphics g) {// gのフィールドはここに書いてある
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g.setFont(font);// フォントをセットする
			drawSea(g);// 海チップの描画
			mapEnd();// カーソルマップ端移動処理

			g.setColor(Color.WHITE);
			if (phase == 0) {// TODO 配置する艦を選択（今後実装）
				isShip[0] = false;
			} else if (phase == 1) {// 艦の召喚　可動状態
				isShip[0] = true;
				posLC.x = pos.x;// posLC =pos;はオブジェクトのコピーではなく参照コピーとなるため×
				posLC.y = pos.y;
				g.drawImage(img[2], pos.x, pos.y, this);

			} else if (quar == true && phase == 2) {// 艦の抜錨　可動状態へ
				isShip[0] = false;
				AffineTransform at = new AffineTransform();
				at.setToRotation(Math.toRadians(-90), 15, 15);
				g2.translate(posLC.x,posLC.y);
				g2.drawImage(img[2],at,this);
				g2.translate(-posLC.x,-posLC.y);

			} else if (phase == 2) {// 艦の投錨　固定状態へ
				isShip[0] = false;
				g.drawImage(img[2], posLC.x, posLC.y, this);
				jl.setText("<HTML>投錨しました。ここに停泊させます。<br>SPACE：抜錨（移動）");


			} else if (phase >= 3 && posLC.x == pos.x && posLC.y == pos.y) {// 艦の抜錨　可動状態へ
				isShip[0] = true;
				g.drawImage(img[2], pos.x, pos.y, this);
				jl.setText("<HTML>抜錨しました。停泊場所を決定してください。<br>SPACE：投錨（停泊）");
				phase = 1;
			} else if (phase >= 3) {// 艦の先端を選べていない　可動状態にするには船首を選ぶ
				g.drawImage(img[2], posLC.x, posLC.y, this);
				jl.setText("<HTML>艦の船首を選べていません。<br>可動状態にするには船首を選んでSPACEを押してください。");
				phase = 2;
			}
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
			if (pos.x < MAPMIN_X) {// マップ左端にカーソルが到達したとき．
				pos.x += 30 * MAPMAX_X;// マップ右端にカーソルを移動させる
			} else if (pos.x > MAPMIN_X + 30 * (MAPMAX_X - 1)) {// マップ
				pos.x -= 30 * MAPMAX_X;
			} else if (isShip[0] == true
					&& pos.y < MAPMIN_Y) {
				pos.y += 30 * (MAPMAX_Y - ship_len[0] + 1);
			} else if (pos.y < MAPMIN_Y) {
				pos.y += 30 * MAPMAX_Y;
			} else if (isShip[0] == true
					&& pos.y > MAPMIN_Y + 30 * (MAPMAX_Y - ship_len[0])) {
				pos.y -= 30 * (MAPMAX_Y - ship_len[0] + 1);
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
		if (key_t[5] == true) {// Z
			if(quar == true){
				quar =false;
			}else{
				quar=true;
			}
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
		case KeyEvent.VK_Z:
			key_t[5] = true;
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
		case KeyEvent.VK_Z:
			key_t[5] = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}

