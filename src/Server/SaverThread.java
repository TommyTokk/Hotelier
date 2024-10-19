package Server;

public class SaverThread implements Runnable{

    private ServerCache cache;

    @Override
    public void run() {
        this.save();
    }

    public void save() {
        cache = ServerCache.getInstance();
        try{
            System.out.println("[SERVER] Saving Users...");
            System.out.println(cache.getAllUsers().size());
            long start = System.currentTimeMillis();
            for (User u : cache.getAllUsers()) {
                JsonHandler.updateUser(ServerMain.getUsersPath(), u);
            }
            long end = System.currentTimeMillis();
            System.out.println("Time for saving users: " + (end - start) + "ms");

            System.out.println("[SERVER] Saving Hotels...");
            System.out.println(cache.getAllHotels().size());
            start = System.currentTimeMillis();
            for (Hotel h : cache.getAllHotels()) {
                JsonHandler.updateHotel(ServerMain.getHotelsPath(), h);
            }
            end = System.currentTimeMillis();
            System.out.println("Time for saving hotels: " + (end - start) + "ms");
            cache.flushCache();
        }catch(Exception e){
            System.err.printf("[SERVER] Error: %s, Line: %d\n", e.getMessage(), e.getStackTrace()[0].getLineNumber());
        }
    }
}
