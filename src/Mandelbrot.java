/*
   This applet draws a representation of the famous Mandelbrot
   set.  It's real purpose, however, is to demonstrate the
   use of a separated thread to do long computations.  The
   thread is not started until the user clicks a "Start" button.
*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Mandelbrot extends JApplet {

   Display canvas;  // The drawing surface on which the Mandelbrot
                    // set is displayed.
                    
   JButton stopButton, startButton;  // Computation will start when
                                     // Start button is pressed and will
                                     // continue until it finishes or the
                                     // user presses the "Stop" button.

   public void init() {
          // Initialize the applet by creating the canvas
          // and buttons and adding them to the applet's
          // content pane.
      
      setBackground(Color.gray);
      
      canvas = new Display();
      getContentPane().add(canvas, BorderLayout.CENTER);
      
      JPanel bottom = new JPanel();
      bottom.setBackground(Color.gray);
      startButton = new JButton("Start");
      startButton.addActionListener(canvas);
      bottom.add(startButton);
      stopButton = new JButton("Stop");
      stopButton.addActionListener(canvas);
      bottom.add(stopButton);
      stopButton.setEnabled(false);
      
      getContentPane().add(bottom, BorderLayout.SOUTH);
      
   } // end init();
   
   
   public Insets getInsets() {
         // Leave space around the applet that will show in the
         // background color, gray.
      return new Insets(2,2,2,2);
   }
   
   public void stop() {
         // This method is called by the system when the applet
         // is about to be temporarily or permanently stopped.
         // To the canvas to stop the computation thread, if
         // it is running.
      canvas.stopRunning();
   }


   // --- The following nested class represents the drawing surface
   // --- of the applet and also does all the work.
   
   class Display extends JPanel implements ActionListener, Runnable {
   
      Image OSI;    // An off-screen images that holds the picture
                    //    of the Mandelbrot set.  This is copied onto
                    //    the drawing surface, if it exists.  It is
                    //    created by the computational thread.
                    
      Graphics OSG; // A graphics context for drawing on OSI.

      Thread runner;    // A thread to do the computation.
      boolean running;  // This is set to true when the thread is running.
      
      double xmin = -2.5;   // The ranges of x and y coordinates that
      double xmax = 1;      //    are represented by this drawing surface
      double ymin = -1.25;
      double ymax = 1.25;
   
      public void paintComponent(Graphics g) {
            // Called by the system to paint the drawing surface.
            // This copies the off-screen image onto the screen,
            // if the off-screen image exists.  If not, it just
            // fills the drawing surface with black.
         if (OSI == null) {
            g.setColor(Color.black);
            g.fillRect(0,0,getWidth(),getHeight());
         }
         else {
            g.drawImage(OSI,0,0,null);
         }
      }
      
      public void actionPerformed(ActionEvent evt) {
            // This will be called when the user clicks on
            // the "Start" or "Stop" button.  It responds
            // by starting or stopping the animation.
         String command = evt.getActionCommand();
         if (command.equals("Start"))
            startRunning();
         else if (command.equals("Stop"))
            stopRunning();
      }
      
      void startRunning() {
           // A simple method that starts the computational thread,
           // unless it is already running.  (This should be
           // impossible since this method is only called when
           // the user clicks the "Start" button, and that button
           // is disabled when the thread is running.)
         if (running)
            return;
         runner = new Thread(this);
              // Creates a thread that will execute the run()
              // method in this Display class.
         running = true;
         runner.start();
      }
      
      void stopRunning() {
           // A simple method that is called to stop the computational
           // thread.  This is done by setting the value of the
           // variable, running.  The thread checks this value
           // regularly and will terminate when running becomes false.
         running = false;
      }
      
      int countIterations(double x, double y) {
            // The Mandelbrot set is represented by coloring
            // each point (x,y) according to the number of
            // iterations it takes before the while loop in 
            // this method ends.  For points that are actually
            // in the Mandelbrot set, or very close to it, the 
            // count will reach the maximum value, 80.  These
            // points will be colored purple.  All other colors
            // represent points that are definitely NOT in the set.
         int count = 0;
         double zx = x;
         double zy = y;
         while (count < 80 && Math.abs(x) < 100 && Math.abs(zy) < 100) {
            double new_zx = zx*zx - zy*zy + x;
            zy = 2*zx*zy + y;
            zx = new_zx;
            count++;
         }
         return count;
      }
      
      int i,j;   // The center pixel of a square that needs to be
                 //    drawing.  These variables are set in the
                 //    run() method of the Display class and are
                 //    used in the run() method of the painter object.
                 //    The same is true for the next two variables.

      int size;  // The size of the square that needs to be drawn.

      int colorIndex;  // A number between 1 and 80 that is used
                       //    to decide on the color of the square.
      
      Runnable painter = new Runnable() {
               // A Runnable object whose job is to paint a
               // square onto the off-screen canvas, and then
               // copy that square onto the screen.  It will do
               // this when its run method is called.  The data
               // for the square are given by the preceding four
               // variables.
            public void run() {
               int left = i - size/2;
               int top = j - size/2;
               OSG.setColor( Color.getHSBColor(colorIndex/100.0F,1F,1F) );
               OSG.fillRect(left,top,size,size);
               paintImmediately(left,top,size,size);
            }
        };
      

      public void run() {
            // This is the run method that is executed by the
            // computational thread.  It draws the Mandelbrot
            // set in a series of passes of increasing resolution.
            // In each pass, it fills the applet with squares
            // that are colored to represent the Mandelbrot set.
            // The size of the squares is cut in half on each pass.

         startButton.setEnabled(false);  // Disable "Start" button
         stopButton.setEnabled(true);    //    and enable "Stop" button
                                         //    while thread is running.

         int width = getWidth();   // Current size of this canvas.
         int height = getHeight();

         OSI = createImage(getWidth(),getHeight());
             // Create the off-screen image where the picture will
             // be stored, and fill it with black to start.
         OSG = OSI.getGraphics();
         OSG.setColor(Color.black);
         OSG.fillRect(0,0,width,height);
         
         for (size = 64; size >= 1 && running; size = size/2) {
               // Outer for loop performs one pass, filling
               // the image with squares of the given size.
               // The size here is given in terms of pixels.
               // Note that all loops end immediately if running
               // becomes false.
            double dx,dy;  // Size of square in real coordinates.
            dx = (xmax - xmin)/width * size;
            dy = (ymax - ymin)/height * size;
            double x = xmin + dx/2;  // x-coord of center of square.
            for (i = size/2; i < width+size/2 && running; i += size) {
                  // First nested for loop draws one column of squares.
               double y = ymax - dy/2; // y-coord of center of square
               for (j = size/2; j < height+size/2 && running; j += size) {
                      // Innermost for loop draws one square, by
                      // counting iterations to determine what
                      // color it should be, and then invoking the
                      // "painter" object to actually draw the square.
                   colorIndex = countIterations(x,y);
                   try {
                      SwingUtilities.invokeAndWait(painter);
                   }
                   catch (Exception e) {
                   }
                   y -= dy;
               }
               x += dx;
               Thread.yield();  // Give other threads a chance to run.
            }
         }

         running = false;  // The thread is about to end, either
                           // because the computation is finished
                           // or because running has been set to
                           // false elsewhere.  In the former case,
                           // we have to set running = false here
                           // to indicate that the thread is no
                           // longer running.

         startButton.setEnabled(true);  // Reset states of buttons.
         stopButton.setEnabled(false);

      } // end run()


   } // end nested class Display
   

} // end class Mandelbrot
