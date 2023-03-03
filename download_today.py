import datetime
import os

import requests
from loguru import logger
from tqdm import tqdm

token = os.environ["token"]
fe_host = os.environ["fe_host"]
bg_host = os.environ["bg_host"]
static_dir = "static"

logger.info("token: {}, fe_host: {}, bg_host: {}, static_dir: {}", token, fe_host, bg_host, static_dir)

today_dir = "{}/{}".format(static_dir, datetime.datetime.now().strftime("%Y-%m-%d"))
fe_prefix = "{}/{}".format(fe_host, today_dir)
backend_url = "{}/img/today?token={}".format(bg_host, token)

logger.info("today: {}, fe_prefix: {}, backend_url: {}", today_dir, fe_prefix, backend_url)
results = requests.get(backend_url).json()

if not os.path.exists(today_dir):
    os.makedirs(today_dir)

logger.info("total: {}", len(results))

for item in tqdm(results):
    # get the url
    url = item.split("(")[1].split(")")[0]

    filename = url.split("/")[-1]
    if os.path.exists("{}/{}".format(today_dir, filename)):
        logger.info("file exists: {}", filename)
        continue

    with open("{}/{}".format(today_dir, filename), "wb") as f:
        f.write(requests.get(url).content)

    item = item.replace(url, "{}/{}".format(fe_prefix, filename))

    with open("{}/record.md".format(today_dir), "a+") as f:
        f.write(item)
        f.write("\n")
        f.write("\n")
    logger.info("done: {}", filename)

logger.info("done")
