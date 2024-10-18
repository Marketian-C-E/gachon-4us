package co.kr.myfitnote.game;

public interface Publisher {

    void subscribe(Subscribe subscribe);
    void notify(int status,String data);
}
