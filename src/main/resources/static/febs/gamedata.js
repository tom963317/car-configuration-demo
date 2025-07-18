layui.define(['febs'],function(exports) {
    let $ = layui.jquery;
    let self = {};
    self.language = {
        sc : '中文',
        en : '英语',
        kn : '泰语',
        vl : '越语',
        tc : '繁体',
        kr : '韩语',
        br : '葡萄牙语',
        in : '印尼语'
    }
    self.clienttype = {
        0 : 'PC',
        1 : 'H5',
        2 : 'PC-COCOS',
        3 : 'H5-COCOS'
    }
    self.chartTimer = undefined;
    self.getClienttypeDisplay = function (type){
        return self.clienttype[type];
    }
    self.getLangDisplay = function (lang_key){
        return self.language[lang_key];
    }
    self.language2select = function (selectKey){
        let $select = $('<select name="language"> </select>');
        for (let key in self.language) {
            let boo = false;
            if(key == selectKey) boo = true;
            let $option = $("<option></option>");
            $option.val(key);
            $option.text(self.language[key]);
            $option.attr("selected", boo);
            $select.append($option);
        }
        return $select;

    }

    self.initChannel = function(data){
        self.channel = data;
        self.getChannelById = function (ids) {
            let idarr = ids.split(",");
            let result = "";
            for(let j = 0; j < idarr.length; j++) {
                for(let i = 0; i < self.channel.length; i++){
                    if(idarr[j] == self.channel[i].id) {
                        result += self.channel[i].loginname;
                        break;
                    }
                }
                if (j < idarr.length - 1) {
                    result += ","
                }
            }
            return result;

        }
    }
/*    layui.febs.get("febs/views/bussiness/fontana_user/list" + '?type=COMPANY&pageSize=100', null, function (data) {
        data.data.reverse();
        data.data.push({'loginname': '所有渠道', 'value':'0','selected' : true});
        data.data.reverse();
        self.channel = data.data;
        self.getChannelById = function (id) {
            debugger;
            self.channel.forEach(function (v,i){
                if(id == v.id) {
                    return v;
                }
            });
        }
    });*/
    self.thirdPartyGame = {
        1 : '区块链'
    }
    exports('gamedata', self);
});
