package com.rockgarden.websocket.serverinfo;

import cn.hutool.core.lang.Dict;
import com.rockgarden.websocket.serverinfo.model.Server;
import com.rockgarden.websocket.serverinfo.payload.ServerVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/server")
public class ServerController {

    @GetMapping
    public Dict serverInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        ServerVO serverVO = ServerUtil.wrapServerVO(server);
        return ServerUtil.wrapServerDict(serverVO);
    }

}
