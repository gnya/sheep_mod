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
- [ ] Happyな羊がドロップする羊毛を増やす
- [ ] Happyな羊が一度に食べる草ブロックの数を増やす

#### Player側の実装

- [ ] 寝ている際のカメラの位置を調整する
- [ ] 寝ている際のプレイヤーの当たり判定がおかしい
- [ ] オフセットの値を乗る側のEntityが指定する
- [ ] LivingEntityに状態SleepInSheepを追加する？
- [ ] プレイヤーが羊の上で寝ると夜を明かせるようにする

#### その他の機能の実装

- [ ] 村人が羊の上で寝るようにする
- [ ] 寝ているEntityの頭からZzzのパーティクルを出す