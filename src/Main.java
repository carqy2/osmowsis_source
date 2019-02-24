/**
 * @author Chang Yang
 */
public class Main {

    public static void main(String[] args) {
        SimulationManager sim = new SimulationManager();
        if (args.length == 0) {
            System.out.println("ERROR: Test scenario file name not found");
        } else {
            sim.readFile(args[0]);
            sim.runSimulation();
//            for (int i = 0; i < 100; i++) {
//
//            }
        }
//        VirtualLawn lawn = new VirtualLawn();
//        System.out.println(lawn.getVirtualLawn().length);





    }
}
