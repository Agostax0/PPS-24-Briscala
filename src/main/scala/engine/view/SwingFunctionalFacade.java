package engine.view;


import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

class SwingFunctionalFacade {

    public static interface Frame {
        Frame setSize(int width, int height);
        Frame addPanel(String panelName, int x, int y, int width, int height);
        Frame addScrollablePanel(String panelName, int x, int y, int width, int height);
        Frame setGridLayout(String panelName, int rows, int columns);
        Frame moveComponentIntoPanel(String componentName, String panelName);
        Frame removeComponentFromPanel(String componentName, String panelName);
        Frame addButton(String text, String name);
        Frame addLabel(String text, String name);
        Frame show();
        Supplier<String> events();        
    }

    public static Frame createFrame(){
        return new FrameImpl();
    }

    private static class FrameImpl implements Frame {
        private final JFrame jframe = new JFrame();
        private final Map<String,JPanel> panels = new HashMap<>();
        private final Map<String, Component> components = new HashMap<>();
        private final LinkedBlockingQueue<String> eventQueue = new LinkedBlockingQueue<>();
        private final Supplier<String> events = () -> {
            try{
                return eventQueue.take();
            } catch (InterruptedException e){
                return "";
            }
        };

        public FrameImpl() {
            this.jframe.setLayout(null);
        }

        @Override
        public Frame setSize(int width, int height) {
            this.jframe.setSize(width, height);
            return this;
        }

        @Override
        public Frame addPanel(String panelName, int x, int y, int width, int height) {
            JPanel jp = new JPanel();
            this.panels.put(panelName, jp);
            jp.setBounds(x, y, width, height);
            jp.setBorder(BorderFactory.createTitledBorder(panelName));
            this.jframe.getContentPane().add(jp);
            return this;
        }

        @Override
        public Frame addScrollablePanel(String panelName, int x, int y, int width, int height) {
            JPanel contentPanel = new JPanel();
            this.panels.put(panelName, contentPanel);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            
            JTextArea text =  new JTextArea(25,40);
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBounds(x, y, width, height);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            
            this.jframe.getContentPane().add(scrollPane);
            return this;
        }

        @Override
        public Frame setGridLayout(String panelName, int rows, int columns) {
            var panel = this.panels.get(panelName);
            panel.setLayout(new GridLayout(rows, columns));
            return this;
        }

        @Override
        public Frame moveComponentIntoPanel(String componentName, String panelName) {
            var panel = this.panels.get(panelName);
            var component = this.components.get(componentName);
            panel.add(component);
            this.jframe.repaint();
            panel.revalidate();
            return this;
        }

        @Override
        public Frame removeComponentFromPanel(String componentName, String panelName) {
            var panel = this.panels.get(panelName);
            var component = this.components.get(componentName);
            panel.remove(component);
            this.jframe.repaint();
            panel.revalidate();
            return this;
        }

        @Override
        public Frame addButton(String text, String name) {
            JButton jb = new JButton(text);
            jb.setActionCommand(name);
            this.components.put(name, jb);
            jb.addActionListener(e -> {
                try {
                    eventQueue.put(name);
                } catch (InterruptedException ex){}
            });
            return this;
        }

        @Override
        public Frame addLabel(String text, String name) {
            JLabel jl = new JLabel(text);
            this.components.put(name, jl);
            return this;
        }

        @Override
        public Supplier<String> events() {
            return events;
        }


        @Override
        public Frame show() {
            this.jframe.setVisible(true);
            return this;
        }

    }
}
