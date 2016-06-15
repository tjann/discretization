/**
 *  GEDPROTOOLS - Gene Expression Data pre PROcessing TOOLS <p>
 *
 *  Latest release available at http://lidecc.cs.uns.edu.ar/files/gedprotools.zip <p>
 *
 *  Copyright (C) 2015 - Cristian A. Gallo <p>
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version. <p>
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 *  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 *  more details. <p>
 *
 *  You should have received a copy of the GNU General Public License along with
 *  this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 *  Place - Suite 330, Boston, MA 02111-1307, USA. <br>
 *  http://www.fsf.org/licenses/gpl.txt
 */

package GEDPROTOOLS;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class GEDPROTOOLSApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {

        ImageIcon imagine;
        java.net.URL imgURL = this.getClass().getResource("resources/gpt.png");
        imagine = new ImageIcon(imgURL);

        GEDPROTOOLSView main = new GEDPROTOOLSView(this);
        JFrame frame = main.getFrame();
        frame.setIconImage(imagine.getImage());
        frame.setSize(1024, 768);
        show(main);

        //show(new GEDPROTOOLSView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of GRNCOP2App
     */
    public static GEDPROTOOLSApp getApplication() {
        return Application.getInstance(GEDPROTOOLSApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(GEDPROTOOLSApp.class, args);
    }
}
