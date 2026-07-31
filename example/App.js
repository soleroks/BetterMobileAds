"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.default = App;
var better_mobile_ads_1 = require("better-mobile-ads");
var expo_1 = require("expo");
var react_native_1 = require("react-native");
function App() {
    var onChangePayload = (0, expo_1.useEvent)(better_mobile_ads_1.default, 'onChange');
    return (<react_native_1.SafeAreaView style={styles.container}>
      <react_native_1.ScrollView style={styles.container}>
        <react_native_1.Text style={styles.header}>Module API Example</react_native_1.Text>
        <Group name="Functions">
          <react_native_1.Text>{better_mobile_ads_1.default.hello()}</react_native_1.Text>
        </Group>
        <Group name="Events">
          <react_native_1.Text>{onChangePayload === null || onChangePayload === void 0 ? void 0 : onChangePayload.value}</react_native_1.Text>
        </Group>
      </react_native_1.ScrollView>
    </react_native_1.SafeAreaView>);
}
function Group(props) {
    return (<react_native_1.View style={styles.group}>
      <react_native_1.Text style={styles.groupHeader}>{props.name}</react_native_1.Text>
      {props.children}
    </react_native_1.View>);
}
var styles = {
    header: { fontSize: 30, margin: 20 },
    groupHeader: { fontSize: 20, marginBottom: 20 },
    group: { margin: 20, backgroundColor: '#fff', borderRadius: 10, padding: 20 },
    container: { flex: 1, backgroundColor: '#eee' },
    view: { flex: 1, height: 200 },
};
