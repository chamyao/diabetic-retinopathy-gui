import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Image;

import javax.swing.UIManager;
import java.awt.SystemColor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import java.awt.Dimension;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI{

	private JFrame frmDiabeticRetinoscopyClassifier;
	private JLabel uihealth;
	private JLabel lblNewLabel_2;
	private JTable table;
	private File image;
	private JLabel lblNormal;
	private boolean loaded=false;
	
	JProgressBar progressBar;
	JLabel lblProgress;
	
	private String[] conditions= {"Condition", "Confidence"};
	private String[][] percentages= {{"Normal", "0.0"},{"DM w/o DR", "0.0"},{"DR", "0.0"}};
	
	private double normal,nodr,dr;
	
	private String prediction=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {       
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmDiabeticRetinoscopyClassifier.setVisible(true);
					window.frmDiabeticRetinoscopyClassifier.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDiabeticRetinoscopyClassifier = new JFrame();
		frmDiabeticRetinoscopyClassifier.setTitle("Diabetic Retinopathy Classifier");
		frmDiabeticRetinoscopyClassifier.getContentPane().setBackground(Color.white);
		frmDiabeticRetinoscopyClassifier.setBounds(100, 100, 868, 615);
		frmDiabeticRetinoscopyClassifier.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDiabeticRetinoscopyClassifier.getContentPane().setLayout(null);
		
		lblNewLabel_2 = new JLabel("");
		lblNewLabel_2.setOpaque(true);
		lblNewLabel_2.setBounds(28, 103, 472, 472);
		
		frmDiabeticRetinoscopyClassifier.getContentPane().add(lblNewLabel_2);		
		JButton btnSelectImage = new JButton("Load Image");
		btnSelectImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser file=new JFileChooser();
                file.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter=new FileNameExtensionFilter("*.Images","jpg","png");
                file.addChoosableFileFilter(filter);
                int result=file.showSaveDialog(null);
                
                if(result==JFileChooser.APPROVE_OPTION)
                {                  
                    image=file.getSelectedFile();
                    String path=image.getAbsolutePath();
                    lblNewLabel_2.setIcon(ResizeImage(path));
                    
                    File copy = new File("/Users/chamyao/eclipse-workspace/PythonToJava/resources/user_image.png");
                    try {
                    	if(copy.exists())
                    		copy.delete();
						Files.copy(image.toPath(), copy.toPath());
						loaded=true;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
                
                else if(result==JFileChooser.CANCEL_OPTION)
                {
                    
                }
			}
		});
		btnSelectImage.setBounds(28, 39, 146, 47);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(btnSelectImage);
		
		JButton btnNewButton = new JButton("Predict");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(loaded)
					worker.execute();
			}
		});
		btnNewButton.setBounds(188, 39, 134, 47);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Result:");
		lblNewLabel.setForeground(UIManager.getColor("Button.light"));
		lblNewLabel.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		lblNewLabel.setBounds(542, 96, 193, 38);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(lblNewLabel);
		
		lblNormal = new JLabel("");
		lblNormal.setFont(new Font("Lucida Grande", Font.BOLD, 15));
		lblNormal.setForeground(new Color(0, 0, 0));
		lblNormal.setBounds(542, 131, 75, 30);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(lblNormal);
		
		progressBar = new JProgressBar();
		
		progressBar.setBounds(357, 54, 484, 37);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(progressBar);
		lblProgress = new JLabel("Progress");
		lblProgress.setForeground(new Color(0, 153, 102));
		lblProgress.setBounds(357, 38, 75, 24);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(lblProgress);
		
		uihealth = new JLabel("");
		uihealth.setBounds(559, 533, 303, 54);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(uihealth);
		uihealth.setIcon(ResizeImage(new ImageIcon(GUI.class.getResource("/resources/ui_health_logo.png"))));
		
		table = new JTable(percentages, conditions);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setRowHeight(48);
		table.setRequestFocusEnabled(false);
		table.setGridColor(new Color(0, 0, 0));
		table.setBounds(542, 269, 243, 144);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(table);
		
		JLabel lblNewLabel_1 = new JLabel("Confidence:");
		lblNewLabel_1.setForeground(SystemColor.controlHighlight);
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		lblNewLabel_1.setBounds(542, 227, 134, 30);
		frmDiabeticRetinoscopyClassifier.getContentPane().add(lblNewLabel_1);	
	}
	
    private ImageIcon ResizeImage(ImageIcon myImage)
    {
        Image img=myImage.getImage();
        Image newImg=img.getScaledInstance(uihealth.getWidth(), uihealth.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image=new ImageIcon(newImg);
        return image;
    }
    
    private ImageIcon ResizeImage(String path)
    {
    	ImageIcon myImage=new ImageIcon(path);
        Image img=myImage.getImage();
        Image newImg=img.getScaledInstance(lblNewLabel_2.getWidth(), lblNewLabel_2.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image=new ImageIcon(newImg);
        return image;
    }
    
	private String getPrediction()
	{
		String prediction=null;
		try{
			Process p = Runtime.getRuntime().exec("//anaconda3/envs/summer_project1/bin/python /Users/chamyao/eclipse-workspace/PythonToJava/resources/test_function.py" );
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			prediction = in.readLine();
		   }
		catch(Exception e) {
			e.printStackTrace();
		}
		String trimmed=prediction.substring(1,prediction.length()-1);
		return trimmed;
	}
	
	private double[] getResult()
	{
		String[] string=getPrediction().split("\\s+");
		double[] result=new double[3];
		for(int i=0; i<string.length; i++)
			result[i]=Double.valueOf(string[i]);
		return result;
	}
	
	SwingWorker<String, Object> worker=new SwingWorker<String, Object>()
	{
		public String doInBackground()
		{
			progressBar.setIndeterminate(true);
			lblProgress.setText("Working");
			
			double[] result=getResult();
			normal=result[0];
			nodr=result[1];
			dr=result[2];
			
			if(normal>nodr && normal>dr)
				prediction="Normal";
			else if(nodr>dr)
				prediction="DM w/o DR";
			else
				prediction="DR";
			
			lblNormal.setText(prediction);
			
			table.setValueAt(String.valueOf(normal), 0,1);
			table.setValueAt(String.valueOf(nodr), 1, 1);
			table.setValueAt(String.valueOf(dr), 2, 1);
			
			lblProgress.setText("Done!");
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
			
			return "success!";
		}
	};	
}
