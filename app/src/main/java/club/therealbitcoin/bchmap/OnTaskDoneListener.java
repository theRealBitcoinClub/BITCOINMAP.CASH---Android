package club.therealbitcoin.bchmap;

public interface OnTaskDoneListener {
    void onTaskDone(String responseData);

    void onError();
}