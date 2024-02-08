package msd.benjones;

import java.util.HashMap;
import java.util.Set;

public class Router {

    private HashMap<Router, Integer> distances;
    private String name;
    public Router(String name) {
        this.distances = new HashMap<>();
        this.name = name;
    }

    public void onInit() throws InterruptedException {
        // 将自己到自己的距离设置为0
        distances.put(this, 0);
        // 对于每个邻居，设置到邻居的距离并发送消息
        for (Neighbor neighbor : Network.getNeighbors(this)) {
            distances.put(neighbor.router, neighbor.cost);
            // 创建并发送消息给邻居
            HashMap<Router, Integer> messageDistances = new HashMap<>();
            messageDistances.put(this, neighbor.cost);
            Message message = new Message(this, neighbor.router, messageDistances);
            Network.sendDistanceMessage(message);
        }
    }


    public void onDistanceMessage(Message message) throws InterruptedException {
        boolean updated = false;
        // 遍历消息中的距离信息
        for (Router router : message.distances.keySet()) {
            if (!router.equals(this)) { // 排除发给自己的信息
                int distanceThroughSender = message.distances.get(router) + distances.getOrDefault(message.sender, Integer.MAX_VALUE);
                if (distanceThroughSender < distances.getOrDefault(router, Integer.MAX_VALUE)) {
                    distances.put(router, distanceThroughSender);
                    updated = true;
                }
            }
        }
        // 如果更新了距离表，则将更新后的信息广播给所有邻居
        if (updated) {
            for (Neighbor neighbor : Network.getNeighbors(this)) {
                HashMap<Router, Integer> messageDistances = new HashMap<>(distances);
                Message newMessage = new Message(this, neighbor.router, messageDistances);
                Network.sendDistanceMessage(newMessage);
            }
        }
    }


    public void dumpDistanceTable() {
        System.out.println("router: " + this);
        for(Router r : distances.keySet()){
            System.out.println("\t" + r + "\t" + distances.get(r));
        }
    }

    @Override
    public String toString(){
        return "Router: " + name;
    }
}
