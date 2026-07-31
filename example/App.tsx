import BetterMobileAds from "better-mobile-ads";
import { Button, SafeAreaView, ScrollView, Text, View } from "react-native";

export default function App() {
  console.log(Object.keys(BetterMobileAds));

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.container}>
        <Text style={styles.header}>BetterMobileAds Test</Text>

        <Group name="SDK">
          <Button
            title="Initialize"
            onPress={() => {
              const result = BetterMobileAds.initialize();

              console.log("INIT:", result);
            }}
          />

          <Button
            title="Version"
            onPress={() => {
              console.log("SDK VERSION:", BetterMobileAds.getVersion());
            }}
          />
        </Group>

        <Group name="Ads">
          <Button
            title="Load Interstitial"
            onPress={async () => {
              try {
                const result = await BetterMobileAds.loadInterstitial();

                console.log("LOAD RESULT:", result);
              } catch (e) {
                console.error("LOAD ERROR:", e);
              }
            }}
          />

          <Button
            title="Show Interstitial"
            onPress={() => {
              try {
                const result = BetterMobileAds.showInterstitial();

                console.log("SHOW RESULT:", result);
              } catch (e) {
                console.error("SHOW ERROR:", e);
              }
            }}
          />
        </Group>
      </ScrollView>
    </SafeAreaView>
  );
}

function Group(props: { name: string; children: React.ReactNode }) {
  return (
    <View style={styles.group}>
      <Text style={styles.groupHeader}>{props.name}</Text>

      {props.children}
    </View>
  );
}

const styles = {
  header: {
    fontSize: 30,
    margin: 20,
  },

  groupHeader: {
    fontSize: 20,
    marginBottom: 20,
  },

  group: {
    margin: 20,
    backgroundColor: "#fff",
    borderRadius: 10,
    padding: 20,
  },

  container: {
    flex: 1,
    backgroundColor: "#eee",
  },
};
