package dpnm.netma.ngom.client.comp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

/**
 * Class Instruction
 *
 * @author Jun Kang
 */
public class StatusBar extends JPanel {
	private JLabel _msg = new JLabel("Message");

	public StatusBar()
	{
		setBorder(new LineBorder(Color.black)) ;
		_msg.setBorder(new EtchedBorder()) ;


		GridBagLayout gridBag = new GridBagLayout() ;
		GridBagConstraints gridBagConst = new GridBagConstraints();
		setLayout(gridBag) ;

		gridBagConst.fill = GridBagConstraints.BOTH ;
		gridBagConst.weightx = 1.0;
		gridBag.setConstraints(_msg, gridBagConst);
		add(_msg) ;
	}

	public void setMessage(String message)
	{
		_msg.setText("State : " + message) ;
	}
}
