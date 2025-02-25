/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package allfonts;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.stream.Stream;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class AllFontsWindows extends JFrame{
    public AllFontsWindows(){
        super("AllFontsWindows");
        Container contenedor = getContentPane();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DefaultTableModel model = new DefaultTableModel(null, new String[]{"family", "name", "postscript name", "font name"});
        JTable table = new JTable(model){
            @Override
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
            
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(200, 200));
        
        Stream.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()).
                map((Font t) -> new String[]{t.getFamily(), t.getName(), t.getPSName(), t.getFontName()}).
                forEach(model::addRow);
        
        contenedor.add(new JScrollPane(table), BorderLayout.CENTER);
        
        setResizable(false);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
