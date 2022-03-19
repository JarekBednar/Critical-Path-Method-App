package boil.cpm;


import algorithm.Action;
import javafx.collections.ObservableList;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Graph
{

    public org.graphstream.graph.Graph graph;

    public Graph()
    {
        this.graph = new SingleGraph("CPM");

        // inluclude css styles
        graph.setAttribute("ui.stylesheet", styleSheet);

        graph.setAutoCreate(true);
        graph.setStrict(false);
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
    }

    protected void sleep()
        {
            try { Thread.sleep(1000); } catch (Exception e) {}
        }

    void markCriticalPath(List<String> a)
    {
        for(int i=0;i<a.size();i++)
        {
                this.graph.getNode(a.get(i)).setAttribute("ui.class", "marked");
                sleep();
        }
    }

    public List inputAdapter(ObservableList<Activity> list )
    {
        List<Action> input_list = new ArrayList<>();
        for(int i=0;i<list.size();i++)
        {

            String action_name= list.get(i).getActivity();
            float duration = list.get(i).getTime();

            Action a = new Action(action_name,duration,new ArrayList<Action>());

            List<Action> sequence = new ArrayList<>();
            for(int j=0;j< list.size();j++)
            {

                if(parseInt(list.get(i).getPrevious_sequence())==parseInt(list.get(j).getNext_sequence()))
                {
                  sequence.add(input_list.get(j));
                }

            }
            a.setPrecedingActions(sequence);
            input_list.add(a);
        }

        return input_list;
    }

    public void generateGraph(ObservableList<Activity> list, List<Action> a)
    {

        for(int i=0;i<a.size();i++)
        {
            String weight = String.valueOf(a.get(i).getDuration())+" "+String.valueOf(a.get(i).getName());
            String action_name= a.get(i).getName();
            String start_node = String.valueOf(a.get(i).getStartEvent());
            String end_node = String.valueOf(a.get(i).getEndEvent())
                    ;
            graph.addEdge(action_name,start_node,end_node,true).setAttribute("length",weight);
            //graph.addEdge("A", "1", "2").setAttribute("length","3 A");
        }

        //node fields
        for (Node node : graph)
        {
            node.setAttribute("ui.label", node.getId());
        }

        //set edges and spites
        SpriteManager sman = new SpriteManager(graph);

        for (int i = 0; i < graph.getEdgeCount(); i++)
        {
            Edge e = graph.getEdge(i);
            e.setAttribute("label", "" +  e.getAttribute("length"));

            Sprite s = sman.addSprite(e.getId());
            s.attachToEdge(e.getId());
            s.setPosition(0.5, 0.15, 0);

            float ES = a.get(i).getEarliestStart();
            float EF = a.get(i).getEarliestFinish();
            float LS = a.get(i).getLatestStart();
            float LF = a.get(i).getLatestFinish();
            float rez = a. get(i).getReserve();

            String data= ES+" - "+EF+" - "+LS+" - "+LF+" - "+rez;

            s.setAttribute("label", data);

        }

        // critical path(nodes)
         markCriticalPath(calculateCriticalPath(a));
    }

    public List<String> calculateCriticalPath(List<Action> a)
    {
         List<String> path = new ArrayList<>();
         for (int i=0;i<a.size();i++)
         {
             if(a.get(i).getPrecedingActions().size()==0) //initial node
             {
                 path.add(String.valueOf(a.get(i).getStartEvent()));
             }
             if(a.get(i).getReserve()==0)
             {
                 path.add(String.valueOf(a.get(i).getEndEvent()));
             }
         }
         return path;
    }

    protected String styleSheet =
            "node   {" +
                    "fill-color: red;" +
                    "size: 50px;" +
                    "text-size: 20px;" +
                    "text-alignment: center;"+

                    "}" +
            "node.marked " +
                    "{" +
                    "fill-color: green;" +
                    "}" +
            "edge " +
                    "{"+
                    "fill-color: yellow;"+
                    "size: 3px;"+
                    "arrow-shape:arrow;"+
                    "arrow-size:10px, 20px;"+
                    "}"+
            "sprite " +
                    "{" +
                    "shape: box;"+
                    "fill-color: pink;"+
                    "size: 30px;" +
                    "size-mode: fit;"+
                    "text-alignment: center;"+
                    "text-size: 10px;" +
                    "}" ;

}
