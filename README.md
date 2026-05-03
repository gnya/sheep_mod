## Sheep Mod

![sleep_in_sheep](https://github.com/user-attachments/assets/865f9c99-6f70-40ce-8c9b-b6c18eee3c10)

羊の上で眠れるようになります（なりたい）

### TODO

#### Sheep側の実装

- [x] Sheepに状態Happyを追加する
- [x] HappyなSheepがスポーンするようにする
- [x] Happyな羊の上に乗る際の位置に補正をいれる
- [x] Happyな羊の場合に当たり判定を大きくする
- [x] 子どもやHappyでない羊の上では眠れないようにする
- [x] Happyな子どもの羊が生まれるようにする
- [x] Happyな羊の鳴き声を低くする
- [x] Happyな羊の体力を32にする
- [x] クリックするときの判定が小さい気がする
- [x] Happyな羊がドロップする羊毛を増やす
- [x] Happyな羊が一度に食べる草ブロックの数を増やす
- [x] Happyな羊が時々羊毛を落とすようにする
- [x] Happyな羊の周りに綿毛が舞うようにする

#### Player側の実装

- [x] LivingEntityに状態SleepInSheepを追加する
- [x] LivingEntityのstartSleeping/stopSleepingを羊に対応させる
- [x] ServerPlayerのstartSleeping/stopSleepingを羊に対応させる
- [x] PlayerのstartSleepInBed/stopSleepInBedを羊に対応させる
- [x] ServerPlayerのstartSleepInBed/stopSleepInBedを羊に対応させる
- [x] プレイヤーが羊の上で寝ると夜を明かせるようにする
- [x] 近くに敵がいる場合は眠れないようにする
- [x] 誰かが既に寝ている場合は眠れないようにする
- [x] 眠れない場合にベッドと同様のメッセージを表示する
- [x] 眠っているとき羊の体力を表示されないようにする
- [x] Shiftで降りる…のメッセージを表示させない
- [x] どこで寝ているか見えないので寝ている際のカメラの位置を調整する
- [x] 寝ている際のプレイヤーの位置がおかしい
- [x] 寝ている際のプレイヤーの当たり判定がおかしい
- [x] オフセットの値を乗る側のEntityが指定する

#### その他の機能の実装

- [ ] 村人が羊の上で寝るようにする
- [ ] 寝ているEntityの頭からZzzのパーティクルを出す