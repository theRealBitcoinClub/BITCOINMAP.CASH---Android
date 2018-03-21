package club.therealbitcoin.bchmap.interfaces;

public interface OnTaskDoneListener {
    void onTaskDone(String responseData);

    void onError();
}