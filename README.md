### 天气预报数据抓取工具

用以抓取中央气象台天气预报数据

功能介绍：
 - 定时抓取图片数据
 - 对图片数据进行裁剪
 - 若抓取失败，邮件通知

JavaScript 版

```javascript
import axios from "axios";
import * as cheerio from "cheerio";

const BASE_URL = "http://www.nmc.cn";
const SOURCE_URL = BASE_URL + "/publish/precipitation/1-day.html";
const BASE_CLASS_NAME =
  ".container .row .col-xs-10 .bgwhite .p-wrap .p-nav.nav2 ul li a";
const IMAGE_ID = "#imgpath";

function getHour(url) {
  if (url != undefined) {
    let fileName = url.substring(
      url.lastIndexOf("_") + 1,
      url.lastIndexOf(".")
    );
    let index = url.lastIndexOf(".")
    let hour = url.substring(index - 5, index) / 100;
    return hour;
  }
  return "";
}

async function getBaseUrl() {
  const response = (await axios.get(SOURCE_URL)).data;
  const $ = cheerio.load(response);
  const featuredUrls = $(BASE_CLASS_NAME);

  let linkUrls = [];
  for (let i = 0; i < featuredUrls.length; i++) {
    let linkUrl = $(featuredUrls[i]).attr("href");
    let url = BASE_URL + linkUrl;
    linkUrls.push(url);
  }

  let imageUrlList = [];
  return axios.all(linkUrls.map((link) => axios.get(link))).then((resList) => {
    resList.forEach((res) => {
      const $ = cheerio.load(res.data);
      const imageDom = $(IMAGE_ID);
      let imageUrl = $(imageDom).attr("src");
      let hour = getHour(imageUrl);

      imageUrlList.push({
        hour,
        imageUrl,
      });
    });
    return imageUrlList;
  });
}

let rainImageList = await getBaseUrl();
//console.log(rainImageList)
export default rainImageList;
```
